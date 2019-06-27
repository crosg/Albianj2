package org.albianj.boot.timer;

import org.albianj.boot.timer.impl.TimerWheel;

public class Test {
    final static ITimer I_ALBIAN_TIMER = new TimerWheel();


    public static void main(String[] args) {

//        RBTree<String> bst = new RBTree<String>(false);
//        bst.addNode("d");
//        bst.addNode("d");
//        bst.addNode("c");
//        bst.addNode("c");
//        bst.addNode("b");
//        bst.addNode("f");
//
//        bst.addNode("a");
//        bst.addNode("a");
//        bst.addNode("a");
//        bst.addNode("e");
//
//        bst.addNode("g");
//        bst.addNode("h");
//
//
//        bst.remove("c");
//
//        bst.printTree(bst.getRoot());
//        System.out.println();
//        RBTreeNode<String> node = bst.pullMinNode(bst.getRoot());
//        System.out.println(node.getValue());
//        bst.printTree(bst.getRoot());
//
//        RBTreeNode<String> node1 = bst.findMinNode(bst.getRoot());
//        System.out.println(node1.getValue());
//        bst.printTree(bst.getRoot());
//        System.out.println();
//
//        bst.remove(node1.getValue());
//        bst.printTree(bst.getRoot());
//        System.out.println();


//        ITimerTask timerTask = new main();

//        java.util.ITimer timer1 = new  java.util.ITimer();
//        timer1.schedule(new java.util.ITimerTask() {
//            @Override
//            public void run() {
//                System.out.println("Synchro data to other servers.");
//            }
//        }, 5000);


//        final long begin = System.currentTimeMillis();
//        for (int i = 0; i < 10; i++) {
//            I_ALBIAN_TIMER.addTimeout(new ITimerTask() {
//                @Override
//                public void run(ITimeoutEntry ITimeoutEntry, String argv) throws Exception {
//                    long end = System.currentTimeMillis();
//                    System.out.println("ITimeoutEntry, argv = " + argv + " ITimeoutEntry = " +(end - begin) + "Thread = " +Thread.currentThread().getId());
//                }
//            }, 5, TimeUnit.SECONDS, "" + i);
//        }
//        I_ALBIAN_TIMER.start();
//        I_ALBIAN_TIMER.stop();
    }
//    @Override
//    public void run(ITimeoutEntry timeout, String argv) throws Exception {
//        System.out.println("timeout, argv = " + argv );
//    }
}
