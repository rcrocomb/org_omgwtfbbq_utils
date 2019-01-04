package org.omgwtfbbq.datastructure

import com.google.common.base.Preconditions
import groovy.util.logging.Commons

import java.util.concurrent.atomic.AtomicInteger

@Commons
class BinaryTree<T> {

    Node<T> root

    def add(T newData) {
        Preconditions.checkArgument(newData != null)
        if (!root) {
            // I think parent = null so that we could traverse up and know when to stop more easily?
            root = new Node(data: newData, left: null, right: null, parent: null)
        } else {
            add(root, newData)
        }
    }

    def add(Node node, T newData) {
        if (newData <= node.data) {
            if (node.left) {
                add(node.left, newData)
            } else {
                node.left = new Node(data: newData, left: null, right: null, parent: node)
            }
        } else {
            if (node.right) {
                add(node.right, newData)
            } else {
                node.right = new Node(data: newData, left: null, right: null, parent: node)
            }
        }
    }

    boolean contains(T data) {
        find(data) != null
    }

    /*
        For simple types like integer, find() is basically contains().
     */

    T find(T data) {
        return root ? find(root, data) : null
    }

    // Pre-orderish.
    // TODO: hmm, maybe pass a "matcher closure" rather than use '=='?  So could do 'String.startsWith', etc.
    T find(Node<T> node, T data) {
        if (!node) return null

        if (node.data == data) {
            return node.data
        }

        if (node.left) {
            T result = find(node.left, data)
            if (result != null)
                return result
        }

        if (node.right) {
            T result = find(node.right, data)
            if (result != null)
                return result
        }

        return null
    }

    def preOrder(closure) {
        preOrder(root, closure)
    }

    def preOrder(node, closure) {
        closure(node)
        if (node.left) preOrder(node.left, closure)
        if (node.right) preOrder(node.right, closure)
    }

    def inOrder(closure) {
        inOrder(root, closure)
    }

    def inOrder(node, closure) {
        if (node.left) inOrder(node.left, closure)
        closure(node)
        if (node.right) inOrder(node.right, closure)
    }

    def postOrder(closure) {
        postOrder(root, closure)
    }

    def postOrder(node, closure) {
        if (node.left) postOrder(node.left, closure)
        if (node.right) postOrder(node.right, closure)
        closure(node)
    }

    void draw() {
        // TODO: could pass the Map, I guess.
        // Map is keyed by depth.
        final AtomicInteger theCount = new AtomicInteger(0)
        final Map<Integer, List<Coordinate>> elements = [:]
        def action = { Node<T> node, AtomicInteger count, int depth ->
            if (!elements[depth]) {
                elements[depth] = []
            }
            // new elements always go on the end
            elements[depth] << new Coordinate(depth: depth, count: count.getAndIncrement(), data: node.data)
        }
        // Builds the 'elements' data structure
        draw(root, action, theCount, 0)

        // Uhh, 'i' and 'depth' are the same, right?
        elements.sort { it.key }.eachWithIndex { depth, atThisDepth, i ->
            print "[$depth] "
            atThisDepth.eachWithIndex { coordinate, j ->
                int relativeX = j == 0 ? coordinate.count : coordinate.count - atThisDepth[j - 1].count // TODO: could do a 'previous' instead
                relativeX.times { print "    " }
                print (coordinate.data != null ? coordinate.data : "x")
                if (j + 1 == atThisDepth.size()) print "\n"
            }
        }
        println "-" * 80
    }

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
    T data
}
