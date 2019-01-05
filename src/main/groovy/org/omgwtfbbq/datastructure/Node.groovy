package org.omgwtfbbq.datastructure

class Node<T> {
    T data
    Node<T> left
    Node<T> right
    Node<T> parent

    boolean isLeaf() {
        !(left || right)
    }

    @Override
    String toString() {
        return String.format("%s left %s right %s parent %s", data, left?.data, right?.data, parent != null)
    }
}
