package org.omgwtfbbq.datastructure

import groovy.util.logging.Commons

@Commons
class RedBlackTree<T> extends BinaryTree<T> {

    /*
        TODO: awwh man, 'left' and 'right' and 'parent' are expected to be
        TODO: Node<T>, so asking for color is problematic.  Groovy half-assedness to the rescue.
     */

    static class RedBlackNode<T> extends Node<T> {
        Color color

        boolean isRed() { color == Color.RED }
        boolean isBlack() { color == Color.BLACK }

        Color parentsColor() { parent.color }

        boolean isParentRed() { parent.isRed() }

        RedBlackNode<T> sibling() {
            // No parent or grandparent: can't have an an "uncle/aunt"
            if (!parent) {
                return null
            }

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

        @Override
        String toString() {
            return "color = $color,${super.toString()}"
        }
    }

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
        return new RedBlackNode<>(color: Color.RED, data: newData, left: null, right: null, parent: node)
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

    void restructure(RedBlackNode<T> newNode) {
        def p = newNode.parent
        def gp = p.parent
        int ordering = whichStructure(newNode)
        switch (ordering) {
            case 0:
                // z is fine
                // for v we need to relocate its right child to be 'u's left child
                // for u we just need the above change, plus update its parent
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
     */

    def whichStructure(newNode) {
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

    def whichStructure2(newNode) {
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

        This method only called for trees  tall enough that newNode.parent.parent
        is known okay.
     */

    List inOrderize(RedBlackNode<T> newNode) {
        [newNode, newNode.parent, newNode.parent.parent].sort { it.data }
    }

    protected String printNode(node) { "" + node.data + "/" + (node.color == Color.RED ? "R" : "B") }
}
