package org.omgwtfbbq.datastructure

import groovy.util.logging.Commons
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

@Commons
class RedBlackTreeTests {

    /*
        Neat.  I don't know if I've ever extended a Groovy class to add a data
        member, then written no constructors at all.  So I wanted to see if
        construction just allowed me to have the Map-like one with an extra data
        element or not.  It does!
     */

    @Test
    void test_node_construction_1() {
        new RedBlackTree.RedBlackNode<Integer>(color: RedBlackTree.Color.RED, left: null, right: null, parent: null)
    }

    @Test
    void test_inOrderize_1() {
        def a = new RedBlackTree.RedBlackNode(data: 1)
        def b = new RedBlackTree.RedBlackNode(data: 2)
        def c = new RedBlackTree.RedBlackNode(data: 3)
        a.parent = b
        b.parent = c
        def (x, y, z) = new RedBlackTree().inOrderize(a)
        assertEquals(a, x)
        assertEquals(b, y)
        assertEquals(c, z)
    }

    @Test
    void test_inOrderize_2() {
        def a = new RedBlackTree.RedBlackNode(data: 1)
        def b = new RedBlackTree.RedBlackNode(data: 2)
        def c = new RedBlackTree.RedBlackNode(data: 3)
        c.parent = b
        b.parent = a
        def (x, y, z) = new RedBlackTree().inOrderize(c)
        assertEquals(a, x)
        assertEquals(b, y)
        assertEquals(c, z)
    }

    @Test
    void test_inOrderize_3() {
        def a = new RedBlackTree.RedBlackNode(data: 1)
        def b = new RedBlackTree.RedBlackNode(data: 2)
        def c = new RedBlackTree.RedBlackNode(data: 3)
        a.parent = c
        c.parent = b
        def (x, y, z) = new RedBlackTree().inOrderize(a)
        assertEquals(a, x)
        assertEquals(b, y)
        assertEquals(c, z)
    }


    /*
        This is degenerate left
     */

    @Test
    void test_whichStructure_1() {
        def z = new RedBlackTree.RedBlackNode(data: 1)
        def v = new RedBlackTree.RedBlackNode(data: 2)
        def u = new RedBlackTree.RedBlackNode(data: 3)
        z.parent = v
        v.parent = u
        assertEquals(0, new RedBlackTree().whichStructure(z))
        assertEquals(RedBlackTree.Color.RED, z.color)
        assertEquals(RedBlackTree.Color.RED, u.color)
        assertEquals(RedBlackTree.Color.BLACK, v.color)
    }

    /*
         u
        /
       v
        \
         z
     */

    @Test
    void test_whichStructure_2() {
        def z = new RedBlackTree.RedBlackNode(data: 2)
        def v = new RedBlackTree.RedBlackNode(data: 1)
        def u = new RedBlackTree.RedBlackNode(data: 3)
        z.parent = v
        v.parent = u
        assertEquals(1, new RedBlackTree().whichStructure(z))
        assertEquals(RedBlackTree.Color.BLACK, z.color)
        assertEquals(RedBlackTree.Color.RED, u.color)
        assertEquals(RedBlackTree.Color.RED, v.color)
    }

    /*
        Degenerate right
     */

    @Test
    void test_whichStructure_3() {
        def z = new RedBlackTree.RedBlackNode(data: 3)
        def v = new RedBlackTree.RedBlackNode(data: 2)
        def u = new RedBlackTree.RedBlackNode(data: 1)
        z.parent = v
        v.parent = u
        assertEquals(2, new RedBlackTree().whichStructure(z))
        assertEquals(RedBlackTree.Color.RED, z.color)
        assertEquals(RedBlackTree.Color.RED, u.color)
        assertEquals(RedBlackTree.Color.BLACK, v.color)
    }

    /*
         u
          \
           v
          /
         z
     */

    @Test
    void test_whichStructure_4() {
        def z = new RedBlackTree.RedBlackNode(data: 2)
        def v = new RedBlackTree.RedBlackNode(data: 3)
        def u = new RedBlackTree.RedBlackNode(data: 1)
        z.parent = v
        v.parent = u
        assertEquals(3, new RedBlackTree().whichStructure(z))
        assertEquals(RedBlackTree.Color.BLACK, z.color)
        assertEquals(RedBlackTree.Color.RED, u.color)
        assertEquals(RedBlackTree.Color.RED, v.color)
    }

    /*
        This is degenerate left
     */

    @Test
    void test_whichStructure2_1a() {
        def z = new RedBlackTree.RedBlackNode(data: 1)
        def v = new RedBlackTree.RedBlackNode(data: 2)
        def u = new RedBlackTree.RedBlackNode(data: 3)
        z.parent = v
        v.parent = u
        assertEquals(0, new RedBlackTree().whichStructure2(z))
        assertEquals(RedBlackTree.Color.RED, z.color)
        assertEquals(RedBlackTree.Color.RED, u.color)
        assertEquals(RedBlackTree.Color.BLACK, v.color)
    }

    /*
         u
        /
       v
        \
         z
     */

    @Test
    void test_whichStructure2_2a() {
        def z = new RedBlackTree.RedBlackNode(data: 2)
        def v = new RedBlackTree.RedBlackNode(data: 1)
        def u = new RedBlackTree.RedBlackNode(data: 3)
        z.parent = v
        v.parent = u
        assertEquals(1, new RedBlackTree().whichStructure2(z))
        assertEquals(RedBlackTree.Color.BLACK, z.color)
        assertEquals(RedBlackTree.Color.RED, u.color)
        assertEquals(RedBlackTree.Color.RED, v.color)
    }

    /*
        Degenerate right
     */

    @Test
    void test_whichStructure2_3a() {
        def z = new RedBlackTree.RedBlackNode(data: 3)
        def v = new RedBlackTree.RedBlackNode(data: 2)
        def u = new RedBlackTree.RedBlackNode(data: 1)
        z.parent = v
        v.parent = u
        assertEquals(2, new RedBlackTree().whichStructure2(z))
        assertEquals(RedBlackTree.Color.RED, z.color)
        assertEquals(RedBlackTree.Color.RED, u.color)
        assertEquals(RedBlackTree.Color.BLACK, v.color)
    }

    /*
         u
          \
           v
          /
         z
     */

    @Test
    void test_whichStructure2_4a() {
        def z = new RedBlackTree.RedBlackNode(data: 2)
        def v = new RedBlackTree.RedBlackNode(data: 3)
        def u = new RedBlackTree.RedBlackNode(data: 1)
        z.parent = v
        v.parent = u
        assertEquals(3, new RedBlackTree().whichStructure2(z))
        assertEquals(RedBlackTree.Color.BLACK, z.color)
        assertEquals(RedBlackTree.Color.RED, u.color)
        assertEquals(RedBlackTree.Color.RED, v.color)
    }

    @Test
    void test_pointIt_1() {
        def z = new RedBlackTree.RedBlackNode(data: 1)
        def v = new RedBlackTree.RedBlackNode(data: 2)
        def u = new RedBlackTree.RedBlackNode(data: 3)

        u.left = v
        v.left = z
        v.parent = u

        new RedBlackTree().pointIt(v, z)
        assertEquals(z, u.left)
        assertEquals(u, z.parent)
    }

    @Test
    void test_pointIt_2() {
        def z = new RedBlackTree.RedBlackNode(data: 3)
        def v = new RedBlackTree.RedBlackNode(data: 2)
        def u = new RedBlackTree.RedBlackNode(data: 1)

        u.right = v
        v.right = z
        v.parent = u

        new RedBlackTree().pointIt(v, z)
        assertEquals(z, u.right)
        assertEquals(u, z.parent)
    }

    @Test
    void test_restructure_1() {
        def z = new RedBlackTree.RedBlackNode(data: 10)
        def v = new RedBlackTree.RedBlackNode(data: 5)
        def u = new RedBlackTree.RedBlackNode(data: 1)
        // Uhh, put in some nodes for v.right and u.right
        def vright = new RedBlackTree.RedBlackNode(data: 7)
        def uright = new RedBlackTree.RedBlackNode(data: 12)
        v.right = vright
        u.right = uright

        u.left = v
        v.left = z

        z.parent = v
        v.parent = u
        new RedBlackTree().restructure(z)
        assertNull(z.left)
        assertNull(z.right)
        assertEquals(v, z.parent)
        assertEquals(z, v.left)
        assertEquals(u, v.right)
        // This is a bit disingenuous.  We simply didn't set a parent for 'u'
        assertNull(v.parent)
        // Make sure we put the proper nodes under 'u' when we rotated it
        assertEquals(vright, u.left)
        assertEquals(uright, u.right)
    }

    @Test
    void test_restructure_2() {
        def z = new RedBlackTree.RedBlackNode(data: 7)
        def v = new RedBlackTree.RedBlackNode(data: 5)
        def u = new RedBlackTree.RedBlackNode(data: 10)
        def vleft = new RedBlackTree.RedBlackNode(data: 3)
        def uright = new RedBlackTree.RedBlackNode(data: 12)
        v.left = vleft
        u.right = uright

        u.left = v
        v.right = z

        z.parent = v
        v.parent = u
        new RedBlackTree().restructure(z)

        assertEquals(v, z.left)
        assertEquals(u, z.right)
        assertNull(z.parent)

        assertEquals(vleft, v.left)
        assertNull(v.right)
        assertEquals(z, v.parent)

        assertNull(u.left)
        assertEquals(uright, u.right)
        assertEquals(z, u.parent)
    }

    @Test
    void test_restructure_3() {
        def z = new RedBlackTree.RedBlackNode(data: 10)
        def v = new RedBlackTree.RedBlackNode(data: 7)
        def u = new RedBlackTree.RedBlackNode(data: 5)
        def uleft = new RedBlackTree.RedBlackNode(data: 3)
        def vleft = new RedBlackTree.RedBlackNode(data: 6)
        u.left = uleft
        v.left = vleft

        u.right = v
        v.right = z

        z.parent = v
        v.parent = u
        new RedBlackTree().restructure(z)

        assertNull(z.left)
        assertNull(z.right)
        assertEquals(v, z.parent)

        assertEquals(u, v.left)
        assertEquals(z, v.right)
        assertNull(v.parent)

        assertEquals(uleft, u.left)
        assertEquals(vleft, u.right)
        assertEquals(v, u.parent)
    }

    @Test
    void test_restructure_4() {
        def z = new RedBlackTree.RedBlackNode(data: 7)
        def v = new RedBlackTree.RedBlackNode(data: 10)
        def u = new RedBlackTree.RedBlackNode(data: 5)
        def uleft = new RedBlackTree.RedBlackNode(data: 3)
        def vright = new RedBlackTree.RedBlackNode(data: 12)
        u.left = uleft
        v.right = vright

        u.right = v
        v.left = z

        z.parent = v
        v.parent = u
        new RedBlackTree().restructure(z)

        assertEquals(u, z.left)
        assertEquals(v, z.right)
        assertNull(z.parent)

        assertNull(v.left)
        assertEquals(vright, v.right)
        assertEquals(z, v.parent)

        assertEquals(uleft, u.left)
        assertNull(u.right)
        assertEquals(z, u.parent)
    }

    @Test
    void test_add_1() {
        RedBlackTree<Integer> tree = new RedBlackTree<>()
        assertTrue(tree.isEmpty())
        tree.add(5)
        assertFalse(tree.isEmpty())
        assertEquals(RedBlackTree.Color.BLACK, tree.root.color)
    }

    @Test
    void test_add_2() {
        RedBlackTree<Integer> tree = new RedBlackTree<>()
        assertTrue(tree.isEmpty())
        tree.add(5)
        tree.add(3)
        assertFalse(tree.isEmpty())
        assertEquals(RedBlackTree.Color.BLACK, tree.root.color)
        assertEquals(RedBlackTree.Color.RED, tree.root.left.color)
        tree.add(7)
        assertEquals(RedBlackTree.Color.RED, tree.root.right.color)
    }

    @Test
    void test_add_3() {
        RedBlackTree<Integer> tree = new RedBlackTree<>()
        assertTrue(tree.isEmpty())
        tree.add(5)
        tree.add(3)
        assertFalse(tree.isEmpty())
        assertEquals(RedBlackTree.Color.BLACK, tree.root.color)
        assertEquals(RedBlackTree.Color.RED, tree.root.left.color)
        tree.add(7)
        assertEquals(RedBlackTree.Color.RED, tree.root.right.color)
        tree.add(1)
        // That required a re-coloring since '3' and '1' are both red
        // But because '7' is red (sibling of 3), we can just recolor
        assertEquals(RedBlackTree.Color.BLACK, tree.root.color)
        assertEquals(RedBlackTree.Color.BLACK, tree.root.right.color)
        assertEquals(RedBlackTree.Color.BLACK, tree.root.left.color)
        // The new node.
        assertEquals(RedBlackTree.Color.RED, tree.root.left.left.color)
    }

    /*
        Data structures book has a bunch of inserts into a single tree with the
        color/structure of the tree at each step
     */

    @Test
    void test_add_4() {
        RedBlackTree<Integer> tree = new RedBlackTree<>()
        assertTrue(tree.isEmpty())
        tree.add(4)
        assertEquals(RedBlackTree.Color.BLACK, tree.root.color)
        assertNull(tree.root.left)
        assertNull(tree.root.right)
        tree.add(7)
        assertEquals(RedBlackTree.Color.BLACK, tree.root.color)
        assertNull(tree.root.left)
        def seven = tree.root.right
        assertEquals(RedBlackTree.Color.RED, seven.color)
        assertNull(seven.left)
        assertNull(seven.right)
        tree.add(12)
        // rebalanced
        assertEquals(RedBlackTree.Color.BLACK, tree.root.color)
        assertEquals(7, tree.root.data)
        def four = tree.root.left
        assertEquals(RedBlackTree.Color.RED, four.color)
        assertNull(four.left)
        assertNull(four.right)
        def twelve = tree.root.right
        assertEquals(RedBlackTree.Color.RED, twelve.color)
        assertNull(twelve.left)
        assertNull(twelve.right)
        tree.add(15)
        // recolor
        assertEquals(RedBlackTree.Color.BLACK, tree.root.color)
        assertEquals(RedBlackTree.Color.BLACK, four.color)
        assertEquals(RedBlackTree.Color.BLACK, twelve.color)
        def fifteen = twelve.right
        assertEquals(RedBlackTree.Color.RED, fifteen.color)
        assertNull(fifteen.left)
        assertNull(fifteen.right)
        tree.add(3)
        def three = four.left
        assertEquals(RedBlackTree.Color.RED, three.color)
        assertNull(three.left)
        assertNull(three.right)
        tree.add(5)
        def five = four.right
        assertEquals(RedBlackTree.Color.RED, five.color)
        assertNull(five.left)
        assertNull(five.right)
        tree.add(14)
        // rebalance
        def fourteen = seven.right
        assertEquals(14, fourteen.data)
        assertEquals(RedBlackTree.Color.BLACK, fourteen.color)
        assertEquals(RedBlackTree.Color.RED, twelve.color)
        assertEquals(RedBlackTree.Color.RED, fifteen.color)
        assertEquals(twelve, fourteen.left)
        assertEquals(fifteen, fourteen.right)
        assertNull(twelve.left)
        assertNull(twelve.right)
        assertNull(fifteen.left)
        assertNull(fifteen.right)
        tree.add(18)
        // recolor
        assertEquals(RedBlackTree.Color.RED, fourteen.color)
        assertEquals(RedBlackTree.Color.BLACK, twelve.color)
        assertEquals(RedBlackTree.Color.BLACK, fifteen.color)
        def eighteen = fifteen.right
        assertEquals(18, eighteen.data)
        assertEquals(RedBlackTree.Color.RED, eighteen.color)
        assertNull(eighteen.left)
        assertNull(eighteen.right)
        tree.add(16)
        // rebalance
        def sixteen = fourteen.right
        assertEquals(16, sixteen.data)
        assertEquals(RedBlackTree.Color.BLACK, sixteen.color)
        assertEquals(RedBlackTree.Color.RED, fifteen.color)
        assertEquals(RedBlackTree.Color.RED, eighteen.color)
        assertEquals(fifteen, sixteen.left)
        assertEquals(eighteen, sixteen.right)
        tree.add(17)
        // rebalance recursively: big changes
        assertTrue(fourteen.isRoot())
        assertEquals(RedBlackTree.Color.BLACK, fourteen.color)
        assertEquals(seven, fourteen.left)
        assertEquals(RedBlackTree.Color.RED, seven.color)
        assertEquals(twelve, seven.right)
        assertEquals(RedBlackTree.Color.BLACK, twelve.color)
        assertEquals(four, seven.left)
        assertEquals(RedBlackTree.Color.BLACK, four.color)
        assertEquals(RedBlackTree.Color.RED, three.color)
        assertEquals(RedBlackTree.Color.RED, five.color)
        assertEquals(sixteen, fourteen.right)
        assertEquals(RedBlackTree.Color.RED, sixteen.color)
        assertEquals(fifteen, sixteen.left)
        assertEquals(RedBlackTree.Color.BLACK, fifteen.color)
        assertEquals(eighteen, sixteen.right)
        assertEquals(RedBlackTree.Color.BLACK, eighteen.color)
        def seventeen = eighteen.left
        assertEquals(17, seventeen.data)
        assertEquals(RedBlackTree.Color.RED, seven.color)
        assertNull(seventeen.left)
        assertNull(seventeen.right)
        tree.draw()
    }

    /*
                5                 10
               / \               /  \
              3   10     -->    5    12
                  / \          / \
                 7   12       3   7
     */

    @Test
    void test_leftRotate_1() {
        doLeftRotateTest(false)
    }

    @Test
    void test_leftRotate_1w() {
        doLeftRotateTest(true)
    }

    private static void doLeftRotateTest(boolean useWacky) {
        def tree = new RedBlackTree<Integer>()
        tree.add(5)
        tree.add(3)
        tree.add(10)
        tree.add(7)
        tree.add(12)
        tree.draw()

        if (useWacky) {
            tree.wackyLeft(tree.root)
        } else {
            tree.leftRotate(tree.root)
        }
        tree.draw()

        // See if we messed up the basic stuff.
        def traversal = []
        def output = tree.inOrder({ node -> traversal << node.data })
        assertTrue(output.is(traversal))
        assertEquals([3, 5, 7, 10, 12], output)

        def node = tree.root
        assertEquals(10, node.data)
        assertNotNull(node.left)
        def left = node.left
        assertEquals(5, left.data)
        assertNotNull(left.left)
        assertEquals(3, left.left.data)
        assertNotNull(left.right)
        assertEquals(7, left.right.data)

        assertNotNull(node.right)
        assertEquals(12, node.right.data)
        assertNull(node.right.left)
        assertNull(node.right.right)

    }


    /*
            10               8
            / \             / \
           8   12   -->    7   10
          / \                 /  \
         7   9               9    12
     */

    @Test
    void test_rightRotate_1() {
        doRightRotateTest(false)
    }

    @Test
    void test_RightRotate_1w() {
        doRightRotateTest(true)
    }

    private static void doRightRotateTest(boolean useWacky) {
        def tree = new RedBlackTree<Integer>()
        tree.add(10)
        tree.add(8)
        tree.add(12)
        tree.add(7)
        tree.add(9)
        tree.draw()

        if (useWacky) {
            tree.wackyRight(tree.root)
        } else {
            tree.rightRotate(tree.root)
        }
        tree.draw()

        // See if we messed up the basic stuff.
        def traversal = []
        def output = tree.inOrder({ node -> traversal << node.data })
        assertTrue(output.is(traversal))
        assertEquals([7, 8, 9, 10, 12], output)

        def node = tree.root
        assertEquals(8, node.data)

        assertNotNull(node.left)
        def left = node.left
        assertEquals(7, left.data)
        assertNull(node.left.left)
        assertNull(node.left.right)

        assertNotNull(node.right)
        def right = node.right
        assertEquals(10, right.data)
        assertNotNull(right.left)
        assertEquals(9, right.left.data)
        assertNotNull(right.right)
        assertEquals(12, right.right.data)
    }
}
