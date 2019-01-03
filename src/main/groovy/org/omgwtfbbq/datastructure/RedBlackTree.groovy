package org.omgwtfbbq.datastructure

import groovy.util.logging.Commons

@Commons
class RedBlackTree<T> extends BinaryTree<T> {

    def add(T newData) {
        if (!root) {
            // TODO: parent should point to null or itself? (No null parents in that case.  But maybe null is a stopping condition?)
            root = new Node(data: newData, left: null, right: null, parent: null)
            return
        }

        add(root, newData)
    }

    def add(Node node, T newData) {
        if (newData <= node.data) {
            // add left
        } else {
            // >, add right.
        }
    }
}
