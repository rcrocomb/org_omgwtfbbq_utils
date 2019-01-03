package org.omgwtfbbq.datastructure

import groovy.util.logging.Commons
import org.testng.annotations.Test

@Commons
class BinaryTreeTests {

    @Test
    void test_add_1() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(1)
        tree.draw()
    }

    @Test
    void test_add_2() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(1)
        tree.add(3)
        tree.add(2)
        tree.draw()
    }

    @Test
    void test_add_3() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(3)
        tree.add(1)
        tree.draw()
    }

    @Test
    void test_add_4() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(3)
        tree.add(2)
        tree.add(1)
        tree.draw()
    }
}
