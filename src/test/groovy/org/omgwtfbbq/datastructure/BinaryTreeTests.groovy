package org.omgwtfbbq.datastructure

import groovy.util.logging.Commons
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertEquals

@Commons
class BinaryTreeTests {

    @Test
    void test_add_1() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(1)

        Node<Integer> root = tree.root
        assertNotNull(root)
        assertNull(root.left)
        assertNull(root.right)
        assertNull(root.parent)
        assertEquals(1, root.data)
    }

    @Test
    void test_add_2() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(1)

        Node<Integer> root = tree.root
        assertNotNull(root)
        assertNotNull(root.left)
        assertNull(root.right)
        assertNull(root.parent)
        assertEquals(2, root.data)

        Node<Integer> left = root.left
        assertNotNull(left)
        assertNull(left.left)
        assertNull(left.right)
        assertNotNull(left.parent)
        assertEquals(root, left.parent)
        assertEquals(1, left.data)
    }

    @Test
    void test_add_3() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(1)
        tree.add(2)

        Node<Integer> root = tree.root
        assertNotNull(root)
        assertNull(root.left)
        assertNotNull(root.right)
        assertNull(root.parent)
        assertEquals(1, root.data)

        Node<Integer> right = root.right
        assertNotNull(right)
        assertNull(right.left)
        assertNull(right.right)
        assertNotNull(right.parent)
        assertEquals(root, right.parent)
        assertEquals(2, right.data)
    }

    /*
        Degenerate tree  1 -> 2 -> 3
     */

    @Test
    void test_add_4() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(1)
        tree.add(2)
        tree.add(3)

        Node<Integer> root = tree.root
        assertNotNull(root)
        assertNull(root.left)
        assertNotNull(root.right)
        assertNull(root.parent)
        assertEquals(1, root.data)

        Node<Integer> right = root.right
        assertNotNull(right)
        assertNotNull(right.right)
        assertNotNull(right.parent)
        assertEquals(root, right.parent)
        assertEquals(2, right.data)

        Node<Integer> parent = right
        right = right.right
        assertNotNull(right)
        assertNull(right.left)
        assertNull(right.right)
        assertNotNull(right.parent)
        assertEquals(parent, right.parent)
        assertEquals(3, right.data)
    }

    /*
        Balanced tree
         2
        1 3
     */

    @Test
    void test_add_5() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(1)
        tree.add(3)

        Node<Integer> root = tree.root
        assertNotNull(root)
        assertNotNull(root.left)
        assertNotNull(root.right)
        assertNull(root.parent)
        assertEquals(2, root.data)

        Node<Integer> left = root.left
        assertNotNull(left)
        assertNull(left.left)
        assertNull(left.right)
        assertNotNull(left.parent)
        assertEquals(root, left.parent)
        assertEquals(1, left.data)

        Node<Integer> right = root.right
        assertNotNull(right)
        assertNull(right.right)
        assertNotNull(right.parent)
        assertEquals(root, right.parent)
        assertEquals(3, right.data)


    }
}
