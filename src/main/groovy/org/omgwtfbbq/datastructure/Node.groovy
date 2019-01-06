package org.omgwtfbbq.datastructure

class Node<T> implements Comparable<T> {
    T data
    Node<T> left
    Node<T> right
    Node<T> parent

    boolean isLeaf() {
        !(left || right)
    }

    /*
        A bit sketchy.  Only call on nodes known to be in the tree, i.e. newly-constructed
        nodes will also say "true".
     */

    boolean isRoot() {
        return parent == null
    }

    boolean isLeftSubchild() {
        return isWhich("left")
    }

    boolean isRightSubchild() {
        return isWhich("right")
    }

    // tee-hee: this is probably slooow compared to two separate methods
    // Note 'is': we don't actually look at the data of the node.  I think this is okay...
    boolean isWhich(String which) {
        parent && parent."$which" && parent."$which".is(this)
    }

    @Override
    String toString() {
        return String.format("%s left %s right %s parent %s", data, left?.data, right?.data, parent != null)
    }

    @Override
    int compareTo(T o) {
        if (data == null && o.data == null) {
            return 0
        } else if (data != null) {
            return 1
        } else if (o.data != null) {
            return -1
        } else {
            return data.compareTo(o.data)
        }
    }

    @Override
    int hashCode() {
        return data ? data.hashCode() : 17
    }

    @Override
    boolean equals(Object o) {
        if (!(o instanceof T)) {
            return false
        }
        return this.compareTo((T)o)
        // Uhh, this isn't calling compareTo, above.  I confuzed
//        return this <=> (T) o
    }
}
