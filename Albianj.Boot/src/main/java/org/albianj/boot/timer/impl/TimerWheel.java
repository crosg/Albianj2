package org.albianj.boot.timer.impl;

import org.albianj.boot.AlbianApplicationServant;
import org.albianj.boot.BundleContext;
import org.albianj.boot.helpers.TypeServant;
import org.albianj.boot.tags.BundleSharingTag;
import org.albianj.boot.timer.ITimeoutEntry;
import org.albianj.boot.timer.ITimer;
import org.albianj.boot.timer.ITimerTask;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

@BundleSharingTag
public class TimerWheel implements ITimer {

    private static final AtomicIntegerFieldUpdater<TimerWheel> workerStateUpdater;
    static {
                workerStateUpdater = AtomicIntegerFieldUpdater.newUpdater(TimerWheel.class, "workerState");
    }

    private final Worker worker = new Worker();
    private Thread workerThread = null;

    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    @SuppressWarnings({ "unused", "FieldMayBeFinal", "RedundantFieldInitialization" })
    private volatile int workerState = WORKER_STATE_INIT; // 0 - init, 1 - started, 2 - shut down

    private final long loopTimestamp;
    private final HashedWheelBucket[] wheel;
    private final int mask;
    private final CountDownLatch semaphore = new CountDownLatch(1);
    private final Queue<HashedWheelITimeoutEntry> timeouts = new ConcurrentLinkedQueue<>();// if can use multi-producer single customer
    private final Queue<HashedWheelITimeoutEntry> cancelledTimeouts = new ConcurrentLinkedQueue<>();

    private volatile long startTime;

    public TimerWheel() {
        this(AlbianApplicationServant.Instance.getCurrentBundleContext());
    }

    public TimerWheel(long loopTimestamp, TimeUnit unit) {
        this(AlbianApplicationServant.Instance.getCurrentBundleContext(), loopTimestamp, unit);
    }

    public TimerWheel(long loopTimestamp, TimeUnit unit, int wheelCount) {
        this(AlbianApplicationServant.Instance.getCurrentBundleContext(), loopTimestamp, unit, wheelCount);
    }


    public TimerWheel(BundleContext ctx) {
        this(ctx, 100, TimeUnit.MILLISECONDS);
    }


    public TimerWheel(
            BundleContext ctx, long loopTimestamp, TimeUnit unit) {
        this(ctx, loopTimestamp, unit, 512);
    }


    public TimerWheel(
            BundleContext ctx,
            long loopTimestamp, TimeUnit unit, int wheelCount) {

        if (ctx == null) {
            throw new NullPointerException("threadFactory");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (loopTimestamp <= 0) {
            throw new IllegalArgumentException("loopTimestamp must be greater than 0: " + loopTimestamp);
        }
        if (wheelCount <= 0) {
            throw new IllegalArgumentException("wheelCount must be greater than 0: " + wheelCount);
        }

        // Normalize wheelCount to power of two and initialize the wheel.
        wheel = createWheel(wheelCount);
        mask = wheel.length - 1;

        // Convert loopTimestamp to nanos.
        this.loopTimestamp = unit.toNanos(loopTimestamp);

        // Prevent overflow.
        if (this.loopTimestamp >= Long.MAX_VALUE / wheel.length) {
            throw new IllegalArgumentException(String.format(
                    "loopTimestamp: %d (expected: 0 < loopTimestamp in nanos < %d",
                    loopTimestamp, Long.MAX_VALUE / wheel.length));
        }
            workerThread = ctx.newThread("TimerWheel",worker);
    }

    private static HashedWheelBucket[] createWheel(int ticksPerWheel) {
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException(
                    "ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }
        if (ticksPerWheel > 1073741824) {
            throw new IllegalArgumentException(
                    "ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
        }

        ticksPerWheel = normalizeTicksPerWheel(ticksPerWheel);
        HashedWheelBucket[] wheel = new HashedWheelBucket[ticksPerWheel];
        for (int i = 0; i < wheel.length; i ++) {
            wheel[i] = new HashedWheelBucket();
        }
        return wheel;
    }

    private static int normalizeTicksPerWheel(int ticksPerWheel) {
        int normalizedTicksPerWheel = 1;
        while (normalizedTicksPerWheel < ticksPerWheel) {
            normalizedTicksPerWheel <<= 1;
        }
        return normalizedTicksPerWheel;
    }

    /**
     * Starts the background thread explicitly.  The background thread will
     * start automatically on demand even if you did not call this method.
     *
     * @throws IllegalStateException if this I_ALBIAN_TIMER has been
     *                               {@linkplain #stop() stopped} already
     */
    public void start() {
        switch (workerStateUpdater.get(this)) {
            case WORKER_STATE_INIT:
                if (workerStateUpdater.compareAndSet(this, WORKER_STATE_INIT, WORKER_STATE_STARTED)) {
                    workerThread.start();
                }
                break;
            case WORKER_STATE_STARTED:
                break;
            case WORKER_STATE_SHUTDOWN:
                throw new IllegalStateException("cannot be started once stopped");
            default:
                throw new Error("Invalid WorkerState");
        }

        // Wait until the startTime is initialized by the worker.
        while (startTime == 0) {
            try {
                semaphore.await();
            } catch (InterruptedException ignore) {
                // Ignore - it will be ready very soon.
            }
        }
    }

    @Override
    public Set<ITimeoutEntry> stop() {
        if (Thread.currentThread() == workerThread) {
            throw new IllegalStateException(
                    TimerWheel.class.getSimpleName() +
                            ".stop() cannot be called from " +
                            ITimerTask.class.getSimpleName());
        }

        if (!workerStateUpdater.compareAndSet(this, WORKER_STATE_STARTED, WORKER_STATE_SHUTDOWN)) {
            // workerState can be 0 or 2 at this moment - let it always be 2.
            workerStateUpdater.set(this, WORKER_STATE_SHUTDOWN);

//            if (leak != null) {
//                leak.close();
//            }

            return Collections.emptySet();
        }

        boolean interrupted = false;
        while (workerThread.isAlive()) {
            workerThread.interrupt();
            try {
                workerThread.join(100);
            } catch (InterruptedException ignored) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

//        if (leak != null) {
//            leak.close();
//        }
        return worker.unprocessedTimeouts();
    }

    @Override
    public ITimeoutEntry addTimeout(ITimerTask task, long delay, TimeUnit unit, String argv) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        start();

        // Add the timeout to the timeout queue which will be processed on the next tick.
        // During processing all the queued HashedWheelTimeouts will be added to the correct HashedWheelBucket.
        long deadline = System.nanoTime() + unit.toNanos(delay) - startTime;
        HashedWheelITimeoutEntry timeout = new HashedWheelITimeoutEntry(this, task, deadline, argv);
        timeouts.add(timeout);
        return timeout;
    }

    private final class Worker implements Runnable {
        private final Set<ITimeoutEntry> unprocessedIAlbianTimeoutEntries = new HashSet<ITimeoutEntry>();

        private long tick;

        @Override
        public void run() {
            // Initialize the startTime.
            startTime = System.nanoTime();
            if (startTime == 0) {
                // We use 0 as an indicator for the uninitialized value here, so make sure it's not 0 when initialized.
                startTime = 1;
            }

            // Notify the other threads waiting for the initialization at start().
            semaphore.countDown();

            do {
                final long deadline = waitForNextTick();
                if (deadline > 0) {
                    int idx = (int) (tick & mask);
                    processCancelledTasks();
                    HashedWheelBucket bucket =
                            wheel[idx];
                    transferTimeoutsToBuckets();
                    bucket.expireTimeouts(deadline);
                    tick++;
                }
            } while (workerStateUpdater.get(TimerWheel.this) == WORKER_STATE_STARTED);

            // Fill the unprocessedIAlbianTimeoutEntries so we can return them from stop() method.
            for (HashedWheelBucket bucket: wheel) {
                bucket.clearTimeouts(unprocessedIAlbianTimeoutEntries);
            }
            for (;;) {
                HashedWheelITimeoutEntry timeout = timeouts.poll();
                if (timeout == null) {
                    break;
                }
                if (!timeout.isCancelled()) {
                    unprocessedIAlbianTimeoutEntries.add(timeout);
                }
            }
            processCancelledTasks();
        }

        private void transferTimeoutsToBuckets() {
            // transfer only max. 100000 timeouts per tick to prevent a thread to stale the workerThread when it just
            // adds new timeouts in a loop.
            for (int i = 0; i < 100000; i++) {
                HashedWheelITimeoutEntry timeout = timeouts.poll();
                if (timeout == null) {
                    // all processed
                    break;
                }
                if (timeout.state() == HashedWheelITimeoutEntry.ST_CANCELLED) {
                    // Was cancelled in the meantime.
                    continue;
                }

                long calculated = timeout.deadline / loopTimestamp;
                timeout.remainingRounds = (calculated - tick) / wheel.length;

                final long ticks = Math.max(calculated, tick); // Ensure we don't schedule for past.
                int stopIndex = (int) (ticks & mask);

                HashedWheelBucket bucket = wheel[stopIndex];
                bucket.addTimeout(timeout);
            }
        }

        private void processCancelledTasks() {
            for (;;) {
                HashedWheelITimeoutEntry timeout = cancelledTimeouts.poll();
                if (timeout == null) {
                    // all processed
                    break;
                }
                try {
                    timeout.remove();
                } catch (Throwable t) {
//                    if (logger.isWarnEnabled()) {
//                        logger.warn("An exception was thrown while process a cancellation task", t);
//                    }
                }
            }
        }

        /**
         * calculate goal nanoTime from startTime and current tick number,
         * then wait until that goal has been reached.
         * @return Long.MIN_VALUE if received a shutdown request,
         * current time otherwise (with Long.MIN_VALUE changed by +1)
         */
        private long waitForNextTick() {
            long deadline = loopTimestamp * (tick + 1);

            for (;;) {
                final long currentTime = System.nanoTime() - startTime;
                long sleepTimeMs = (deadline - currentTime + 999999) / 1000000;

                if (sleepTimeMs <= 0) {
                    if (currentTime == Long.MIN_VALUE) {
                        return -Long.MAX_VALUE;
                    } else {
                        return currentTime;
                    }
                }

                // Check if we run on windows, as if thats the case we will need
                // to round the sleepTime as workaround for a bug that only affect
                // the JVM if it runs on windows.
                //
                // See https://github.com/netty/netty/issues/356
//                if (PlatformDependent.isWindows()) {
                    if (AlbianApplicationServant.Instance.isWindows()) {
                    sleepTimeMs = sleepTimeMs / 10 * 10;
                }

                try {
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException ignored) {
                    if (workerStateUpdater.get(TimerWheel.this) == WORKER_STATE_SHUTDOWN) {
                        return Long.MIN_VALUE;
                    }
                }
            }
        }

        public Set<ITimeoutEntry> unprocessedTimeouts() {
            return Collections.unmodifiableSet(unprocessedIAlbianTimeoutEntries);
        }
    }

    private static final class HashedWheelITimeoutEntry implements ITimeoutEntry {

        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;
        private static final AtomicIntegerFieldUpdater<HashedWheelITimeoutEntry> STATE_UPDATER;

        static {
            AtomicIntegerFieldUpdater<HashedWheelITimeoutEntry> updater =
                    AtomicIntegerFieldUpdater.newUpdater(HashedWheelITimeoutEntry.class, "state");
//            if (updater == null) {
//                updater = AtomicIntegerFieldUpdater.newUpdater(HashedWheelITimeoutEntry.class, "state");
//            }
            STATE_UPDATER = updater;
        }

        private final TimerWheel timer;
        private final ITimerTask task;
        private final long deadline;

        @SuppressWarnings({"unused", "FieldMayBeFinal", "RedundantFieldInitialization" })
        private volatile int state = ST_INIT;

        // remainingRounds will be calculated and set by Worker.transferTimeoutsToBuckets() before the
        // HashedWheelITimeoutEntry will be added to the correct HashedWheelBucket.
        long remainingRounds;
        String argv;

        // This will be used to chain timeouts in HashedWheelTimerBucket via a double-linked-list.
        // As only the workerThread will act on it there is no need for synchronization / volatile.
        HashedWheelITimeoutEntry next;
        HashedWheelITimeoutEntry prev;

        // The bucket to which the timeout was added
        HashedWheelBucket bucket;

        HashedWheelITimeoutEntry(TimerWheel timer, ITimerTask task, long deadline, String argv) {
            this.timer = timer;
            this.task = task;
            this.deadline = deadline;
            this.argv = argv;

        }

        @Override
        public ITimer timer() {
            return timer;
        }

        @Override
        public ITimerTask task() {
            return task;
        }

        @Override
        public boolean cancel() {
            // only update the state it will be removed from HashedWheelBucket on next tick.
            if (!compareAndSetState(ST_INIT, ST_CANCELLED)) {
                return false;
            }
            // If a task should be canceled we put this to another queue which will be processed on each tick.
            // So this means that we will have a GC latency of max. 1 tick duration which is good enough. This way
            // we can make again use of our MpscLinkedQueue and so minimize the locking / overhead as much as possible.
            timer.cancelledTimeouts.add(this);
            return true;
        }

        void remove() {
            HashedWheelBucket bucket = this.bucket;
            if (bucket != null) {
                bucket.remove(this);
            }
        }

        public boolean compareAndSetState(int expected, int state) {
            return STATE_UPDATER.compareAndSet(this, expected, state);
        }

        public int state() {
            return state;
        }

        @Override
        public boolean isCancelled() {
            return state() == ST_CANCELLED;
        }

        @Override
        public boolean isExpired() {
            return state() == ST_EXPIRED;
        }

        public void expire() {
            if (!compareAndSetState(ST_INIT, ST_EXPIRED)) {
                return;
            }

            try {
                task.run(this, argv);
            } catch (Throwable t) {
//                if (logger.isWarnEnabled()) {
//                    logger.warn("An exception was thrown by " + ITimerTask.class.getSimpleName() + '.', t);
//                }
            }
        }

        @Override
        public String toString() {
            final long currentTime = System.nanoTime();
            long remaining = deadline - currentTime + timer.startTime;

            StringBuilder buf = new StringBuilder(192)
                    .append(TypeServant.Instance.getSimpleClassName(this.getClass()))
//                    .append(StringUtil.simpleClassName(this))
                    .append('(')
                    .append("deadline: ");
            if (remaining > 0) {
                buf.append(remaining)
                        .append(" ns later");
            } else if (remaining < 0) {
                buf.append(-remaining)
                        .append(" ns ago");
            } else {
                buf.append("now");
            }

            if (isCancelled()) {
                buf.append(", cancelled");
            }

            return buf.append(", task: ")
                    .append(task())
                    .append(')')
                    .toString();
        }
    }

    /**
     * Bucket that stores HashedWheelTimeouts. These are stored in a linked-list like datastructure to allow easy
     * removal of HashedWheelTimeouts in the middle. Also the HashedWheelITimeoutEntry act as nodes themself and so no
     * extra object creation is needed.
     */
    private static final class HashedWheelBucket {
        // Used for the linked-list datastructure
        private HashedWheelITimeoutEntry head;
        private HashedWheelITimeoutEntry tail;

        /**
         * Add {@link HashedWheelITimeoutEntry} to this bucket.
         */
        public void addTimeout(HashedWheelITimeoutEntry timeout) {
            assert timeout.bucket == null;
            timeout.bucket = this;
            if (head == null) {
                head = tail = timeout;
            } else {
                tail.next = timeout;
                timeout.prev = tail;
                tail = timeout;
            }
        }

        /**
         * Expire all {@link HashedWheelITimeoutEntry}s for the given {@code deadline}.
         */
        public void expireTimeouts(long deadline) {
            HashedWheelITimeoutEntry timeout = head;

            // process all timeouts
            while (timeout != null) {
                boolean remove = false;
                if (timeout.remainingRounds <= 0) {
                    if (timeout.deadline <= deadline) {
                        timeout.expire();
                    } else {
                        // The timeout was placed into a wrong slot. This should never happen.
                        throw new IllegalStateException(String.format(
                                "timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    }
                    remove = true;
                } else if (timeout.isCancelled()) {
                    remove = true;
                } else {
                    timeout.remainingRounds --;
                }
                // store reference to next as we may null out timeout.next in the remove block.
                HashedWheelITimeoutEntry next = timeout.next;
                if (remove) {
                    remove(timeout);
                }
                timeout = next;
            }
        }

        public void remove(HashedWheelITimeoutEntry timeout) {
            HashedWheelITimeoutEntry next = timeout.next;
            // remove timeout that was either processed or cancelled by updating the linked-list
            if (timeout.prev != null) {
                timeout.prev.next = next;
            }
            if (timeout.next != null) {
                timeout.next.prev = timeout.prev;
            }

            if (timeout == head) {
                // if timeout is also the tail we need to adjust the entry too
                if (timeout == tail) {
                    tail = null;
                    head = null;
                } else {
                    head = next;
                }
            } else if (timeout == tail) {
                // if the timeout is the tail modify the tail to be the prev node.
                tail = timeout.prev;
            }
            // null out prev, next and bucket to allow for GC.
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
        }

        /**
         * Clear this bucket and return all not expired / cancelled {@link ITimeoutEntry}s.
         */
        public void clearTimeouts(Set<ITimeoutEntry> set) {
            for (;;) {
                HashedWheelITimeoutEntry timeout = pollTimeout();
                if (timeout == null) {
                    return;
                }
                if (timeout.isExpired() || timeout.isCancelled()) {
                    continue;
                }
                set.add(timeout);
            }
        }

        private HashedWheelITimeoutEntry pollTimeout() {
            HashedWheelITimeoutEntry head = this.head;
            if (head == null) {
                return null;
            }
            HashedWheelITimeoutEntry next = head.next;
            if (next == null) {
                tail = this.head =  null;
            } else {
                this.head = next;
                next.prev = null;
            }

            // null out prev and next to allow for GC.
            head.next = null;
            head.prev = null;
            head.bucket = null;
            return head;
        }
    }
}
