package org.omgwtfbbq.datastructure

import groovy.util.logging.Commons
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

@Commons
class BinaryTreeTests {

    @Test
    void test_add_0() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        assertThrows(IllegalArgumentException, {
            tree.add(null)
        })
    }

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

    @Test
    void test_find_1() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(1)
        tree.add(3)
        tree.add(4)
        tree.add(5)

        assertNotNull(tree.find(1))
        assertNotNull(tree.find(2))
        assertNotNull(tree.find(3))
        assertNotNull(tree.find(4))
        assertNotNull(tree.find(5))
        assertNull(tree.find(0))
        assertNull(tree.find(null))
    }

    @Test
    void test_contains_1() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(1)
        tree.add(3)
        tree.add(4)
        tree.add(5)

        assertTrue(tree.contains(1))
        assertTrue(tree.contains(2))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(5))
        assertFalse(tree.contains(0))
        assertFalse(tree.contains(null))
    }

    /*
             2
            / \
           1   4
              / \
             3   5
     */

    @Test
    void test_preOrder_1() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(1)
        tree.add(4)
        tree.add(3)
        tree.add(5)

        def traversal = []
        def output = tree.preOrder({ node -> traversal << node.data })
        assertTrue(output.is(traversal))
        assertEquals([2, 1, 4, 3, 5], output)
    }

    /*
             2
            / \
           1   4
              / \
             3   5
     */

    @Test
    void test_inOrder_1() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(1)
        tree.add(4)
        tree.add(3)
        tree.add(5)

        def traversal = []
        def output = tree.inOrder({ node -> traversal << node.data })
        assertTrue(output.is(traversal))
        assertEquals([1, 2, 3, 4, 5], output)
    }

    @Test
    void test_traversals_1() {
        new BinaryTree<>().preOrder({})
        new BinaryTree<>().inOrder({})
        new BinaryTree<>().postOrder({})
    }

    /*
             2
            / \
           1   4
              / \
             3   5
     */

    @Test
    void test_postOrder_1() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(1)
        tree.add(4)
        tree.add(3)
        tree.add(5)

        def traversal = []
        def output = tree.postOrder({ node -> traversal << node.data })
        assertTrue(output.is(traversal))
        assertEquals([1, 3, 5, 4, 2], output)
    }

    @Test
    void test_remove_1() {
        Integer value
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(2)
        tree.add(1)
        tree.add(4)
        tree.add(3)
        tree.add(5)

        value = tree.remove(2)
        def traversal = []
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 3, 4, 5], traversal)
        assertEquals(2, value)

        value = tree.remove(4)
        traversal = []
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 3, 5], traversal)
        assertEquals(4, value)
    }

    @Test
    void test_remove_2() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.remove(1)

        tree.add(1)
        tree.remove(1)

        tree.add(2)
        tree.add(3)
        tree.remove(2)
        tree.remove(3)
    }

    /*
        Degenerate right.
     */

    @Test
    void test_remove_3() {
        Integer value
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(1)
        tree.add(2)
        tree.add(3)
        tree.add(4)
        tree.add(5)
        tree.add(6)

        value = tree.remove(3)
        def traversal = []
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 2, 4, 5, 6], traversal)
        assertEquals(3, value)

        value = tree.remove(6)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 2, 4, 5], traversal)
        assertEquals(6, value)

        value = tree.remove(2)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 4, 5], traversal)
        assertEquals(2, value)

        value = tree.remove(1)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([4, 5], traversal)
        assertEquals(1, value)

        value = tree.remove(5)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([4], traversal)
        assertEquals(5, value)

        value = tree.remove(4)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([], traversal)
        assertEquals(4, value)
    }

    /*
        Degenerate left
     */

    @Test
    void test_remove_4() {
        Integer value
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(6)
        tree.add(5)
        tree.add(4)
        tree.add(3)
        tree.add(2)
        tree.add(1)

        value = tree.remove(3)
        def traversal = []
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 2, 4, 5, 6], traversal)
        assertEquals(3, value)

        value = tree.remove(6)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 2, 4, 5], traversal)
        assertEquals(6, value)

        value = tree.remove(2)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 4, 5], traversal)
        assertEquals(2, value)

        value = tree.remove(1)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([4, 5], traversal)
        assertEquals(1, value)

        value = tree.remove(5)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([4], traversal)
        assertEquals(5, value)

        value = tree.remove(4)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([], traversal)
        assertEquals(4, value)
    }

    /*
        Uhh, I guess this is degenerate left as well
     */

    @Test
    void test_remove_5() {
        BinaryTree<Integer> tree = new BinaryTree<>()
        tree.add(1)
        tree.add(1)
        tree.add(1)
        tree.add(1)
        tree.add(1)
        tree.add(1)

        tree.remove(1)
        def traversal = []
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 1, 1, 1, 1], traversal)

        tree.remove(1)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 1, 1, 1], traversal)

        tree.remove(1)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 1, 1], traversal)

        tree.remove(1)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1, 1], traversal)

        tree.remove(1)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([1], traversal)

        tree.remove(1)
        traversal.clear()
        tree.inOrder({ node -> traversal << node.data })
        assertEquals([], traversal)
    }

    @Test
    void test_draw_1() {
        new BinaryTree<Integer>().draw()
    }
}
