package org.omgwtfbbq.datastructure

import groovy.util.logging.Commons

/*
    Good ol' self-balancing Red/Black tree, using algorithms largely based on
    those in "Data Structures and Algorithms in Java (3rd edition)" by
    Goodrich and Tamassia.

    I probably wouldn't use kernel-style single if()s lacking braces in professional
    code, but I kinda enjoy it.
 */

@Commons
class RedBlackTree<T> extends BinaryTree<T> {

    static class RedBlackNode<T> extends Node<T> {
        Color color

        boolean isRed() { color == Color.RED }
        boolean isBlack() { color == Color.BLACK }
        boolean isParentRed() { parent.isRed() }

        RedBlackNode<T> sibling() {
            // No parent or grandparent: can't have an an "uncle/aunt"
            if (!parent) return null

            (RedBlackNode)(parent.left.is(this) ? parent.right : parent.left)
        }

        Color parentsSiblingsColor() {
            // No parent or grandparent: can't have an an "uncle/aunt"
            if (!parent || !parent.parent) {
                log.warn("Parent is null or GP is null: no sibling color for $this")
                return null
            }

            // Whee: nulls are considered Black -- the algorithm I'm using has
            // a sentinel node type thing where are "external nodes" are Black
            parent.sibling()?.color ?: Color.BLACK
        }

        @Override String toString() { "color = $color,${super.toString()}" }
    }

    // I prefer an enum over the boolean 'isRed' or similar, though that has the
    // advantage of never being null and an if-else is sufficient, whereas I feel
    // the need to cover my ass w/ an enum.  Ah well.
    enum Color {
        RED, BLACK
    }

    void add(T newData) {
        if (!root) {
            root = new RedBlackNode<>(color: Color.BLACK, data: newData, left: null, right: null, parent: null)
            return
        }

        RedBlackNode<T> node = super.add(root, newData)
        fixDoubleRed(node)
    }

    /*
        Non-root new nodes are always red.
     */

    protected doNewNode(RedBlackNode<T> node, T newData) {
        new RedBlackNode<>(color: Color.RED, data: newData, left: null, right: null, parent: node)
    }

    void fixDoubleRed(RedBlackNode<T> newNode) {
        if (newNode.isRoot())
            return
        if (!newNode.isParentRed())
            return

        // double red: this node and its parent.  Gotta rotate
        Color color = newNode.parentsSiblingsColor()
        if (color == null) {
            throw new RuntimeException("Couldn't determine parent's sibling's color for node $newNode")
        } else if (color == Color.BLACK) {
            restructure(newNode)
        } else if (color == Color.RED) {
            // recolor
            RedBlackNode<T> gp = newNode.parent.parent
            newNode.parent.color = Color.BLACK
            newNode.parent.sibling().color = Color.BLACK
            if (gp.isRoot())
                return
            gp.color = Color.RED
            fixDoubleRed(gp)
        } else {
            throw new RuntimeException("Sky is falling")
        }
    }

    /*
            x                 y
           / \               / \
          1   y      -->    x   3
             / \           / \
             2  3         1   2

        Page 278 of 'Introduction to Algorithms 2nd Edition'
     */

    void leftRotate(RedBlackNode<T> x) {
        // Turn right subchild's left subtree into node's right subtree
        def y = x.right
        x.right = y.left
        if (y.left) {
            y.left.parent = x
        }
        // So now x.right points to '2' and '2' points back to x
        y.parent = x.parent
        if (x.isRoot()) {
            root = y
        } else {
            if (x.isLeftSubchild()) {
                x.parent.left = y
            } else {
                x.parent.right = y
            }

        }
        // Put 'x' on 'y's left.
        y.left = x
        x.parent = y
    }

    /*
            x                 y
           / \               / \
          y   3      -->    1   x
         / \                   / \
        1   2                 2   3
     */

    void rightRotate(RedBlackNode<T> x) {
        def y = x.left
        x.left = y.right
        if (y.right) {
            y.right.parent = x
        }
        y.parent = x.parent
        if (x.isRoot()) {
            root = y
        } else {
            if (x.isLeftSubchild()) {
                x.parent.left = y
            } else {
                x.parent.right = y
            }
        }
        y.right = x
        x.parent = y
    }

    // Significant commonality/symmetry in the methods means we can smoosh them
    void wackyLeft(RedBlackNode<T> x)  { rotate(x, "left")  }
    void wackyRight(RedBlackNode<T> x) { rotate(x, "right") }

    void rotate(RedBlackNode<T> x, dir) {
        def opposite = dir == "left" ? "right" : "left"
        def y = x."$opposite"
        x."$opposite" = y."$dir"
        if (y."$dir") {
            y."$dir".parent = x
        }
        y.parent = x.parent
        if (x.isRoot()) {
            root = y
        } else {
            if (x.isLeftSubchild()) {
                x.parent.left = y
            } else {
                x.parent.right = y
            }
        }
        y."$dir" = x
        x.parent = y
    }

    /*
        based on page 458.  Deets are in whichStructure()

        Dood.  They actually have pseudocode on page 425: who knew?  I mean,
        they said "trinode restructuring", which I presumed was just a term
        of art and not a "hey, remember when we told you how to do this 30
        page ago?"  I guess I should've figured they'd be pretty specific in
        a book like this.  Teach me not to read the whole chapter.
     */

    void restructure(RedBlackNode<T> newNode) {
        def p = newNode.parent
        def gp = p.parent
        int ordering = whichStructure(newNode)
        switch (ordering) {
            case 0:
                pointIt(gp, p)
                gp.left = p.right
                p.right = gp
                gp.parent = p
                break
            case 1:
                pointIt(gp, newNode)
                gp.left = null
                p.right = null
                newNode.left = p
                newNode.right = gp
                p.parent = newNode
                gp.parent = newNode
                break
            case 2:
                pointIt(gp, p)
                gp.right = p.left
                p.left = gp
                gp.parent = p
                break
            case 3:
                pointIt(gp, newNode)
                gp.right = null
                p.left = null
                newNode.left = gp
                newNode.right = p
                p.parent = newNode
                gp.parent = newNode
                break
            default:
                throw new RuntimeException("Unknown ordering $ordering restructuring for node $newNode")
        }
    }

    /*
        Figure out of the subtree with root 'u' is the left or right subchild of
        its parent, and then swap in 'z' as that subchild and set 'z's parent to
        'u's parent.
     */

    void pointIt(RedBlackNode u, RedBlackNode z) {
        if (!u.isRoot()) {
            if (u.isLeftSubchild()) {
                u.parent.left = z
            } else {
                u.parent.right = z
            }
        } else {
            root = z
        }
        z.parent = u.parent
    }

    /*
        page 458

        0:             1:         2:           3:
                u            u        u              u
               / \          / \      / \            / \
              v   4        v   4    4   v          4   v
             / \          / \          / \            /
            z   3        3   z        3   z          z
           / \              / \          / \        / \
          1   2            1   2        1   2      1   2

        where 'u' is the grand parent, 'v' is the parent, and 'z' is the new node

        ... uhhh '1' and '2' are always null, right?  It's a new node.

        Given this, the idea is to re-order these nodes such that, if the nodes
        were sorted by value (really, the order they'd be encountered in an
        in-order traversal), and considered 'a' -> 'b' -> 'c' in this fashion,
        that 'b' becomes the new sub-tree root and 'a' its left child and 'c' its
        right child, i.e.:
          b
         / \
        a   c
     */

    def whichStructure(RedBlackNode<T> newNode) {
        def inOrder = inOrderize(newNode)
        // recolor them now, who cares?
        inOrder[0].color = Color.RED
        inOrder[1].color = Color.BLACK
        inOrder[2].color = Color.RED

        // Above orderings as you'd encounter them in an in-order traverse
        def p = newNode.parent
        def gp = p.parent
        def orderings = [[newNode, p, gp], [p, newNode, gp], [gp, p, newNode], [gp, newNode, p]]
        // What does the structure of the subtree look like?
        for (int i = 0; i < orderings.size(); ++i) {
            if (inOrder == orderings[i]) {
                return i
            }
        }
        throw new RuntimeException("What crazy ordering is $inOrder that it's not in the list of possible orderings?!")
    }

    /*
        Wheee!  The above, but using a single-use Map.  Probably takes longer
        to construct the Map than it takes to do the (on average) 2.5 loop
        iterations.
    */

    def whichStructure2(RedBlackNode<T> newNode) {
        def inOrder = inOrderize(newNode)
        // recolor them now, who cares?
        inOrder[0].color = Color.RED
        inOrder[1].color = Color.BLACK
        inOrder[2].color = Color.RED

        // Above orderings as you'd encounter them in an in-order traverse
        def p = newNode.parent
        def gp = p.parent
        return [[newNode, p, gp]: 0,
                [p, newNode, gp]: 1,
                [gp, p, newNode]: 2,
                [gp, newNode, p]: 3
        ][inOrder]
    }

    /*
        Put newNode, its parent, and its grandparent in sorted order (by data)

        This method only called for trees tall enough that newNode.parent.parent
        is known okay.
     */

    def inOrderize(RedBlackNode<T> newNode) { [newNode, newNode.parent, newNode.parent.parent].sort { it.data } }

    protected String printNode(node) { "" + node.data + "/" + (node.color == Color.RED ? "R" : "B") }
}
