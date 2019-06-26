package org.albianj.boot.timer;

/**
 * 红黑树，目前不支持同key具有多个value
 * @param <T>
 */
public class RBTree<T extends Comparable<T>> {
    private final RBTreeNode<T> root;
    //node number
    private java.util.concurrent.atomic.AtomicLong size =
            new java.util.concurrent.atomic.AtomicLong(0);

    //in overwrite mode,all node's value can not  has same	value
    //in non-overwrite mode,node can have same value, suggest don't use non-overwrite mode.
    private volatile boolean overrideMode = true;

    public RBTree() {
        this.root = new RBTreeNode<T>();
    }

    public RBTree(boolean overrideMode) {
        this();
        this.overrideMode = overrideMode;
    }

    public boolean isOverrideMode() {
        return overrideMode;
    }

    public void setOverrideMode(boolean overrideMode) {
        this.overrideMode = overrideMode;
    }

    /**
     * number of tree number
     *
     * @return
     */
    public long getSize() {
        return size.get();
    }

    /**
     * get the root node
     *
     * @return
     */
    public RBTreeNode<T> getRoot() {
        return root.getLeft();
    }

    /**
     * add value to a new node,if this value exist in this tree,
     * if value exist,it will return the exist value.otherwise return null
     * if override mode is true,if value exist in the tree,
     * it will override the old value in the tree
     *
     * @param value
     * @return
     */
    public T addNode(T value) {
        RBTreeNode<T> t = new RBTreeNode<T>(value);
        return addNode(t);
    }

    /**
     * find the value by give value(include key,key used for search,
     * other field is not used,@see compare method).if this value not exist return null
     *
     * @param value
     * @return
     */
    public T find(T value) {
        RBTreeNode<T> dataRoot = getRoot();
        while (dataRoot != null) {
            int cmp = dataRoot.getValue().compareTo(value);
            if (cmp < 0) {
                dataRoot = dataRoot.getRight();
            } else if (cmp > 0) {
                dataRoot = dataRoot.getLeft();
            } else {
                return dataRoot.getValue();
            }
        }
        return null;
    }

    /**
     * remove the node by give value,if this value not exists in tree return null
     *
     * @param value include search key
     * @return the value contain in the removed node
     */
    public T remove(T value) {
        RBTreeNode<T> dataRoot = getRoot();
        RBTreeNode<T> parent = root;

        while (dataRoot != null) {
            int cmp = dataRoot.getValue().compareTo(value);
            if (cmp < 0) {
                parent = dataRoot;
                dataRoot = dataRoot.getRight();
            } else if (cmp > 0) {
                parent = dataRoot;
                dataRoot = dataRoot.getLeft();
            } else {
                if (dataRoot.getRight() != null) {
                    RBTreeNode<T> min = pullMinNode(dataRoot.getRight());
                    //x used for fix color balance
                    RBTreeNode<T> x = min.getRight() == null ? min.getParent() : min.getRight();
                    boolean isParent = min.getRight() == null;

                    min.setLeft(dataRoot.getLeft());
                    setParent(dataRoot.getLeft(), min);
                    if (parent.getLeft() == dataRoot) {
                        parent.setLeft(min);
                    } else {
                        parent.setRight(min);
                    }
                    setParent(min, parent);

                    boolean curMinIsBlack = min.isBlack();
                    //inherit dataRoot's color
                    min.setRed(dataRoot.isRed());

                    if (min != dataRoot.getRight()) {
                        min.setRight(dataRoot.getRight());
                        setParent(dataRoot.getRight(), min);
                    }
                    //remove a black node,need fix color
                    if (curMinIsBlack) {
                        if (min != dataRoot.getRight()) {
                            fixRemove(x, isParent);
                        } else if (min.getRight() != null) {
                            fixRemove(min.getRight(), false);
                        } else {
                            fixRemove(min, true);
                        }
                    }
                } else {
                    setParent(dataRoot.getLeft(), parent);
                    if (parent.getLeft() == dataRoot) {
                        parent.setLeft(dataRoot.getLeft());
                    } else {
                        parent.setRight(dataRoot.getLeft());
                    }
                    //current node is black and tree is not empty
                    if (dataRoot.isBlack() && !(root.getLeft() == null)) {
                        RBTreeNode<T> x = dataRoot.getLeft() == null
                                ? parent : dataRoot.getLeft();
                        boolean isParent = dataRoot.getLeft() == null;
                        fixRemove(x, isParent);
                    }
                }
                setParent(dataRoot, null);
                dataRoot.setLeft(null);
                dataRoot.setRight(null);
                if (getRoot() != null) {
                    getRoot().setRed(false);
                    getRoot().setParent(null);
                }
                size.decrementAndGet();
                return dataRoot.getValue();
            }
        }
        return null;
    }

    /**
     * find the successor node
     *
     * @param node current node's right node
     * @return
     */
    public RBTreeNode<T> pullMinNode(RBTreeNode<T> node) {
        //find the min node
        RBTreeNode<T> parent = node;
        while (node != null && node.getLeft() != null) {
            parent = node;
            node = node.getLeft();
        }
        //remove min node
        if (parent == node) {
            return node;
        }

        parent.setLeft(node.getRight());
        setParent(node.getRight(), parent);

        //don't remove right pointer,it is used for fixed color balance
        //node.setRight(null);
        return node;
    }

    /**
     * find the successor node
     *
     * @param node current node's right node
     * @return
     */
    public RBTreeNode<T> findMinNode(RBTreeNode<T> node) {
        //find the min node
        RBTreeNode<T> parent = node;
        while (node != null && node.getLeft() != null) {
            parent = node;
            node = node.getLeft();
        }
        return node;
    }

    /**
     * fix remove action
     *
     * @param node
     * @param isParent
     */
    private void fixRemove(RBTreeNode<T> node, boolean isParent) {
        RBTreeNode<T> cur = isParent ? null : node;
        boolean isRed = isParent ? false : node.isRed();
        RBTreeNode<T> parent = isParent ? node : node.getParent();

        while (!isRed && !isRoot(cur)) {
            RBTreeNode<T> sibling = getSibling(cur, parent);
            //sibling is not null,due to before remove tree color is balance

            //if cur is a left node
            boolean isLeft = parent.getRight() == sibling;
            if (sibling.isRed() && !isLeft) {//case 1
                //cur in right
                parent.makeRed();
                sibling.makeBlack();
                rotateRight(parent);
            } else if (sibling.isRed() && isLeft) {
                //cur in left
                parent.makeRed();
                sibling.makeBlack();
                rotateLeft(parent);
            } else if (isBlack(sibling.getLeft()) && isBlack(sibling.getRight())) {//case 2
                sibling.makeRed();
                cur = parent;
                isRed = cur.isRed();
                parent = parent.getParent();
            } else if (isLeft && !isBlack(sibling.getLeft())
                    && isBlack(sibling.getRight())) {//case 3
                sibling.makeRed();
                sibling.getLeft().makeBlack();
                rotateRight(sibling);
            } else if (!isLeft && !isBlack(sibling.getRight())
                    && isBlack(sibling.getLeft())) {
                sibling.makeRed();
                sibling.getRight().makeBlack();
                rotateLeft(sibling);
            } else if (isLeft && !isBlack(sibling.getRight())) {//case 4
                sibling.setRed(parent.isRed());
                parent.makeBlack();
                sibling.getRight().makeBlack();
                rotateLeft(parent);
                cur = getRoot();
            } else if (!isLeft && !isBlack(sibling.getLeft())) {
                sibling.setRed(parent.isRed());
                parent.makeBlack();
                sibling.getLeft().makeBlack();
                rotateRight(parent);
                cur = getRoot();
            }
        }
        if (isRed) {
            cur.makeBlack();
        }
        if (getRoot() != null) {
            getRoot().setRed(false);
            getRoot().setParent(null);
        }

    }
    //get sibling node
    private RBTreeNode<T> getSibling(RBTreeNode<T> node, RBTreeNode<T> parent) {
        parent = node == null ? parent : node.getParent();
        if (node == null) {
            return parent.getLeft() == null ? parent.getRight() : parent.getLeft();
        }
        if (node == parent.getLeft()) {
            return parent.getRight();
        } else {
            return parent.getLeft();
        }
    }
    private boolean isBlack(RBTreeNode<T> node) {
        return node == null || node.isBlack();
    }
    private boolean isRoot(RBTreeNode<T> node) {
        return root.getLeft() == node && node.getParent() == null;
    }
    private T addNode(RBTreeNode<T> node) {
        node.setLeft(null);
        node.setRight(null);
        node.setRed(true);
        setParent(node, null);
        if (root.getLeft() == null) {
            root.setLeft(node);
            //root node is black
            node.setRed(false);
            size.incrementAndGet();
        } else {
            RBTreeNode<T> x = findParentNode(node);
            int cmp = x.getValue().compareTo(node.getValue());

            if (this.overrideMode && cmp == 0) {
                T v = x.getValue();
                x.setValue(node.getValue());
                return v;
            } else if (cmp == 0) {
                //value exists,ignore this node
                return x.getValue();
            }

            setParent(node, x);

            if (cmp > 0) {
                x.setLeft(node);
            } else {
                x.setRight(node);
            }

            fixInsert(node);
            size.incrementAndGet();
        }
        return null;
    }
    /**
     * find the parent node to hold node x,if parent value equals x.value return parent.
     *
     * @param x
     * @return
     */
    private RBTreeNode<T> findParentNode(RBTreeNode<T> x) {
        RBTreeNode<T> dataRoot = getRoot();
        RBTreeNode<T> child = dataRoot;

        while (child != null) {
            int cmp = child.getValue().compareTo(x.getValue());
            if (cmp == 0) {
                return child;
            }
            if (cmp > 0) {
                dataRoot = child;
                child = child.getLeft();
            } else if (cmp < 0) {
                dataRoot = child;
                child = child.getRight();
            }
        }
        return dataRoot;
    }
    /**
     * red black tree insert fix.
     *
     * @param x
     */
    private void fixInsert(RBTreeNode<T> x) {
        RBTreeNode<T> parent = x.getParent();

        while (parent != null && parent.isRed()) {
            RBTreeNode<T> uncle = getUncle(x);
            if (uncle == null) {//need to rotate
                RBTreeNode<T> ancestor = parent.getParent();
                //ancestor is not null due to before before add,tree color is balance
                if (parent == ancestor.getLeft()) {
                    boolean isRight = x == parent.getRight();
                    if (isRight) {
                        rotateLeft(parent);
                    }
                    rotateRight(ancestor);

                    if (isRight) {
                        x.setRed(false);
                        parent = null;//end loop
                    } else {
                        parent.setRed(false);
                    }
                    ancestor.setRed(true);
                } else {
                    boolean isLeft = x == parent.getLeft();
                    if (isLeft) {
                        rotateRight(parent);
                    }
                    rotateLeft(ancestor);

                    if (isLeft) {
                        x.setRed(false);
                        parent = null;//end loop
                    } else {
                        parent.setRed(false);
                    }
                    ancestor.setRed(true);
                }
            } else {//uncle is red
                parent.setRed(false);
                uncle.setRed(false);
                parent.getParent().setRed(true);
                x = parent.getParent();
                parent = x.getParent();
            }
        }
        getRoot().makeBlack();
        getRoot().setParent(null);
    }
    /**
     * get uncle node
     *
     * @param node
     * @return
     */
    private RBTreeNode<T> getUncle(RBTreeNode<T> node) {
        RBTreeNode<T> parent = node.getParent();
        RBTreeNode<T> ancestor = parent.getParent();
        if (ancestor == null) {
            return null;
        }
        if (parent == ancestor.getLeft()) {
            return ancestor.getRight();
        } else {
            return ancestor.getLeft();
        }
    }
    private void rotateLeft(RBTreeNode<T> node) {
        RBTreeNode<T> right = node.getRight();
        if (right == null) {
            throw new java.lang.IllegalStateException("right node is null");
        }
        RBTreeNode<T> parent = node.getParent();
        node.setRight(right.getLeft());
        setParent(right.getLeft(), node);

        right.setLeft(node);
        setParent(node, right);

        if (parent == null) {//node pointer to root
            //right  raise to root node
            root.setLeft(right);
            setParent(right, null);
        } else {
            if (parent.getLeft() == node) {
                parent.setLeft(right);
            } else {
                parent.setRight(right);
            }
            //right.setParent(parent);
            setParent(right, parent);
        }
    }
    private void rotateRight(RBTreeNode<T> node) {
        RBTreeNode<T> left = node.getLeft();
        if (left == null) {
            throw new java.lang.IllegalStateException("left node is null");
        }
        RBTreeNode<T> parent = node.getParent();
        node.setLeft(left.getRight());
        setParent(left.getRight(), node);

        left.setRight(node);
        setParent(node, left);

        if (parent == null) {
            root.setLeft(left);
            setParent(left, null);
        } else {
            if (parent.getLeft() == node) {
                parent.setLeft(left);
            } else {
                parent.setRight(left);
            }
            setParent(left, parent);
        }
    }
    private void setParent(RBTreeNode<T> node, RBTreeNode<T> parent) {
        if (node != null) {
            node.setParent(parent);
            if (parent == root) {
                node.setParent(null);
            }
        }
    }
    /**
     * debug method,it used print the given node and its children nodes,
     * every layer output in one line
     *
     * @param root
     */
    public void printTree(RBTreeNode<T> root) {
        java.util.LinkedList<RBTreeNode<T>> queue = new java.util.LinkedList<RBTreeNode<T>>();
        java.util.LinkedList<RBTreeNode<T>> queue2 = new java.util.LinkedList<RBTreeNode<T>>();
        if (root == null) {
            return;
        }
        queue.add(root);
        boolean firstQueue = true;

        while (!queue.isEmpty() || !queue2.isEmpty()) {
            java.util.LinkedList<RBTreeNode<T>> q = firstQueue ? queue : queue2;
            RBTreeNode<T> n = q.poll();

            if (n != null) {
                String pos = n.getParent() == null ? "" : (n == n.getParent().getLeft()
                        ? " LE" : " RI");
                String pstr = n.getParent() == null ? "" : n.getParent().toString();
                String cstr = n.isRed() ? "R" : "B";
                cstr = n.getParent() == null ? cstr : cstr + " ";
                System.out.print(n + "(" + (cstr) + pstr + (pos) + ")" + "\t");
                if (n.getLeft() != null) {
                    (firstQueue ? queue2 : queue).add(n.getLeft());
                }
                if (n.getRight() != null) {
                    (firstQueue ? queue2 : queue).add(n.getRight());
                }
            } else {
                System.out.println();
                firstQueue = !firstQueue;
            }
        }
    }
}

