package org.omgwtfbbq.datastructure

class Node<T> {
    T data
    Node<T> left
    Node<T> right
    Node<T> parent

    boolean isLeaf() {
        !(left || right)
    }
}
