package org.omgwtfbbq.datastructure

import com.google.common.base.Preconditions
import groovy.util.logging.Commons

import java.util.concurrent.atomic.AtomicInteger

@Commons
class BinaryTree<T> {

    Node<T> root

    boolean isEmpty() { root == null }

    void add(T newData) {
        Preconditions.checkArgument(newData != null)
        if (isEmpty()) {
            // I think parent = null so that we could traverse up and know when to stop more easily?
            root = new Node(data: newData, left: null, right: null, parent: null)
        } else {
            add(root, newData)
        }
    }

    @Override
    Object invokeMethod(String name, Object args) {
        return super.invokeMethod(name, args)
    }

    Node<T> add(Node node, T newData) {
        if (newData <= node.data) {
            if (node.left) {
                add(node.left, newData)
            } else {
                node.left = doNewNode(node, newData)
                return node.left
            }
        } else {
            if (node.right) {
                add(node.right, newData)
            } else {
                node.right = doNewNode(node, newData)
                return node.right
            }
        }
    }

    protected doNewNode(Node<T> node, T newData) { new Node(data: newData, left: null, right: null, parent: node) }

    /*
        Uhhh, remove *first* item w/ this data value, if multiple.
     */

    T remove(T data) { return remove(root, data) }

    T remove(Node<T> treeRoot, T data) {
        Node<T> node = find(treeRoot, data)
        if (!node) return null

        T fromTree = node.data
        if (node.right) {
            Node<T> newSubroot = nextInOrder(node)
            // newSubroot can't be null, can't have non-null left child
            node.data = newSubroot.data
            remove(newSubroot, newSubroot.data)
        } else {
            // no right children of this node: the left subchild is the new root,
            // if any.
            if (node.isRoot()) {
                // we're root of the tree: new root is left subchild
                root = node.left
                if (root) root.parent = null
            } else {
                if (node.isLeftSubchild()) {
                    node.parent.left = node.left
                } else {
                    node.parent.right = node.right
                }
                if (node.left)
                    node.left.parent = node.parent
            }
            // Prune node out of tree
            node.parent = node.left = node.right = null
        }
        return fromTree
    }

    Node<T> nextInOrder(Node<T> node) {
        // in-order traverse
        Node<T> newSubroot = node.right
        while (newSubroot.left) newSubroot = newSubroot.left
        return newSubroot
    }

    boolean contains(T data) { find(data) != null }

    // For simple types like integer, find() is basically contains().
    T find(T data) { !isEmpty() ? find(root, data)?.data : null }

    // Pre-orderish.
    // TODO: hmm, maybe pass a "matcher closure" rather than use '=='?  So could do 'String.startsWith', etc.
    Node<T> find(Node<T> node, T data) {
        if (!node) return null
        if (node.data == data) return node

        if (node.left) {
            Node<T> result = find(node.left, data)
            if (result)
                return result
        }

        if (node.right) {
            Node<T> result = find(node.right, data)
            if (result)
                return result
        }

        return null
    }

    /*
        Take 'x's place in the tree.  If it is root, then 'this' is now root,
        otherwise, replace x as its parent's subchild.
     */

    void reRoot(Node<T> x, Node<T> y) {
        if (x.isRoot()) {
            root = y
        } else {
            x.replaceAsSubChild(y)
        }
    }

    def preOrder(closure) { preOrder(root, closure) }

    def preOrder(node, closure) {
        if (!node) return null
        def ret = closure(node)
        if (node.left) ret = preOrder(node.left, closure)
        if (node.right) ret = preOrder(node.right, closure)
        return ret
    }

    def inOrder(closure) { inOrder(root, closure) }

    def inOrder(node, closure) {
        if (!node) return null
        if (node.left) inOrder(node.left, closure)
        def ret = closure(node)
        if (node.right) ret = inOrder(node.right, closure)
        return ret
    }

    def postOrder(closure) { postOrder(root, closure) }

    def postOrder(node, closure) {
        if (!node) return null
        if (node.left) postOrder(node.left, closure)
        if (node.right) postOrder(node.right, closure)
        return closure(node)
    }

    void draw() {
        if (isEmpty()) {
            println "Empty Tree"
            return
        }
        // TODO: could pass the Map, I guess.
        // Map is keyed by depth.
        final AtomicInteger theCount = new AtomicInteger(0)
        final Map<Integer, List<Coordinate>> elements = [:]
        def action = { Node<T> node, AtomicInteger count, int depth ->
            if (!elements[depth]) {
                elements[depth] = []
            }
            // new elements always go on the end
            elements[depth] << new Coordinate(depth: depth, count: count.getAndIncrement(), data: node)
        }
        // Builds the 'elements' data structure
        draw(root, action, theCount, 0)

        // Uhh, 'i' and 'depth' are the same, right?
        elements.sort { it.key }.eachWithIndex { depth, atThisDepth, i ->
            print "[$depth] "
            atThisDepth.eachWithIndex { coordinate, j ->
                int relativeX = j == 0 ? coordinate.count : coordinate.count - atThisDepth[j - 1].count // TODO: could do a 'previous' instead
                relativeX.times { print "    " }
                print(coordinate.data != null ? printNode(coordinate.data) : "x")
                if (j + 1 == atThisDepth.size()) print "\n"
            }
        }
        println "-" * 80
    }

    protected String printNode(node) { "" + node.data }

    /*
        Bleh.  In order for tree to draw right, we have to increment on null
        children: otherwise spacing is wrong.  This makes nodes look twice as far
        apart as you'd kinda "think" they are and it means that nodes will never
        line up vertically like in a tree where you 1, 3, 2 you'd kinda think:

            1
             \
              3
             /
            2

        such that 1 and 2 are aligned.  But if they "really" have empty child
        nodes between them, then you get:

        1
       / \
      x   \
           \
            3
           / \
          2   x
         /
        x

        where I show nodes that really won't print during the draw call but show
        how now '2' is not under '1' because there's space for the null left
        child of 2.  Whoops, well I guess each node should have it's own X-coordinate,
        really, so...

       1
      x
          3
         2 x
        x

        The drawing algorithm I have requires the null children.  I haven't thought
        about how not to have them and still get things right: the X coordinate in
        their algorithm is simply the number of nodes you've traversed in the in-order
        traverse, but you must count the null child.
    */

    void draw(Node<T> node, action, AtomicInteger count, int depth) {
        if (node.left) draw(node.left, action, count, depth + 1) else count.getAndIncrement()
        action(node, count, depth)
        if (node.right) draw(node.right, action, count, depth + 1) else count.getAndIncrement()
    }
}

/*
    Models the location of a node in a binary tree in order to draw it: depth
    is the depth in the tree (0 at root), and count is the node count for an
    in-order traversal which gives us an X-axis location
 */

class Coordinate<T> {
    int depth // not truly needed, but whatevs
    int count
    Node<T> data
}
