package assignment1;

import java.util.LinkedList;

public class BinarySearchTreeImpl<T> {
    protected static class Node<T> {
        public Node(int key, T value) {
            this.key = key;
            this.value = value;
        }
        public int key;
        public T value;
        public Node<T> parent = null;
        public Node<T> left = null;
        public Node<T> right = null;
    }
    protected Node<T> root = null;

    /** 
     * insert
     * Walks left if smaller, right if bigger until it falls off (current == null), then adds the node there.
     * insert(12) on a tree with root 10 → goes right → adds 12 as 10's right child
     *
     *We walk left when key is smaller and right when key is larger, which maintains the BST property that left < root < right at every node
     *If the key already exists we update the value instead of creating a duplicate, keeping keys unique
     *The new node is always attached at the correct position because we track the last valid parent before falling off 
     * 
     */

    protected void insert(Node<T> x, int key, T value) {
        Node<T> parent = null;
        Node<T> current = x;

        // Walk down the tree until we fall off or find a matching key
        while (current != null) {
            if (key == current.key) {
                // Key already exists: just update the value, no new node needed
                current.value = value;
                return;
            }
            parent = current; // the last valid node we visited
            if (key < current.key) {
                current = current.left;  // left if key smaller
            } else {
                current = current.right; // right if key larger
            }
        }

        // Create the new node to insert
        Node<T> newNode = new Node<>(key, value);

        if (parent == null) {
            // The tree was empty (x was null), so this node becomes the root
            root = newNode;
        } else {
            // Attach the new node to the correct side of its parent
            newNode.parent = parent;
            if (key < parent.key) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }
        }
    }

    /**
     * inorder
     * Travels LEFT → ROOT → RIGHT giving every value back in sorted order.
     * inorder() on tree with 23,1,13,27,35,34 → returns 1,13,23,27,34,35
     * In a BST everything to the LEFT is smaller and everything to the RIGHT is larger
     * By visiting left → root → right recursively, we are guaranteed to always visit smaller values before larger ones
     * This holds at every single node in the tree so the whole output is sorted
     */

    protected LinkedList<T> inorderTreeWalk(Node<T> x) {
        LinkedList<T> result = new LinkedList<>();
        if (x != null) {
            result.addAll(inorderTreeWalk(x.left));  // collect left subtree values
            result.add(x.value);                     // add current node's value
            result.addAll(inorderTreeWalk(x.right)); // collect right subtree values
        }
        return result;
    }

    /**
    * search
    * Walks left or right comparing keys until it finds the node, returns its value.
    * search(12) → walks down the tree → returns "120"
    * 
    * At every node we only go in one direction based on the BST property
     *If the key is smaller we go left because the key cannot exist on the right
     *If the key is larger we go right because the key cannot exist on the left
     *If we fall off the tree the key genuinely does not exist
     */
    protected Node<T> search(Node<T> x, int key) {
        if (x == null || x.key == key) {
            // Base case: either we've fallen off the tree (not found) or we found it
            return x;
        }
        if (key < x.key) {
            return search(x.left, key);  // target is smaller, search left
        } else {
            return search(x.right, key); // target is larger, search right
        }
    }

    /**
     * depth
     * Counts how many levels the tree has including the root.
     * Tree with 23→27→35→34 → returns 3
     * 
     * A null node returns -1 as the base case
     * A single leaf node returns 1 + max(-1, -1) = 0 which is correct
     * At every node we take the taller of the two sides and add 1 for the current level
     * This guarantees we always find the longest path downward
     */
    protected int depth(Node<T> x) {
        if (x == null) {
            return -1; // empty subtree
        }
        int leftDepth  = depth(x.left);
        int rightDepth = depth(x.right);
        return 1 + Math.max(leftDepth, rightDepth); // height is 1 + the taller side
    }

    /**
     * minimum
     * Keeps going left until it can't anymore, that's the smallest.
     * minimum() on tree with 23,1,13 → goes left to 1 → returns 1
     * 
     * The BST property guarantees that smaller values are always to the left
     * So the smallest value must be the leftmost node
     * We keep going left until there is no left child, which must be the minimum
     */
    protected Node<T> minimum(Node<T> x) {
        while (x.left != null) {
            x = x.left; // keep going left until we can't anymore
        }
        return x; // this is the leftmost (smallest) node
    }

    /**
     * maximum
     * Keeps going right until it can't anymore, that's the biggest.
     * maximum() on tree with 23,27,35 → goes right to 35 → returns 35
     * 
     * The mirror argument of minimum
     * Larger values are always to the right in a BST
     * The rightmost node must be the maximum
     */
    protected Node<T> maximum(Node<T> x) {
        while (x.right != null) {
            x = x.right; // keep going right until we can't anymore
        }
        return x; // this is the rightmost (largest) node
    }

    /**
     * successor
     * The next biggest number after the given key in the sorted list.
     * successor(27) in 1,13,23,27,34,35 → returns 34
     * 
     * If a right subtree exists, the successor must be in it because all values there are larger than x. The minimum of that subtree is the smallest of those larger values, making it the immediate successor
     * If no right subtree exists, we climb up until we find an ancestor we approached from the left. That ancestor is the first node larger than x because we were in its left subtree
     */
    protected Node<T> successor(Node<T> x) {
        if (x.right != null) {
            // successor is the minimum of the right subtree
            return minimum(x.right);
        }
        // climb up until we come from a left child
        Node<T> y = x.parent;
        while (y != null && x == y.right) {
            // We came from a right child, so y is still smaller than original x
            x = y;
            y = y.parent;
        }
        return y; // first ancestor for which we came from the left, or null if none
    }

    /**
     * predecessor
     * The next smallest number before the given key in the sorted list.
     * predecessor(13) in 1,13,23,27,34,35 → returns 1
     * 
     * The mirror argument of successor
     * If a left subtree exists, the predecessor is the maximum of it
     * If no left subtree exists, climb up until we find an ancestor we approached from the right
     */
    protected Node<T> predecessor(Node<T> x) {
        if (x.left != null) {
            // predecessor is the maximum of the left subtree
            return maximum(x.left);
        }
        // climb up until we come from a right child
        Node<T> y = x.parent;
        while (y != null && x == y.left) {
            // We came from a left child, so y is still larger than original x
            x = y;
            y = y.parent;
        }
        return y; // first ancestor for which we came from the right, or null if none , if smallest number is Node -1 means nothing exists
    }

    /**
     * delete
     * Finds the node and removes it, if it has two children it gets replaced by its successor.
     * delete(23) where 23 has children 1 and 27 → 27 takes its place
     * 
     * Case 1 and 2 are simple — if only one child exists, that child takes the deleted node's place and the BST property is maintained because all relationships stay the same
     * Case 3 uses the in-order successor which is guaranteed to be larger than everything in the left subtree and smaller than everything else in the right subtree, so placing it at the deleted node's position maintains the BST property perfectly
     * Transplant correctly rewires all parent pointers so no node is left pointing to the deleted node
     */
    protected void delete(Node<T> z) {
        if (z.left == null) {
            // no left child, promote right child (or null) into z's spot
            transplant(z, z.right);
        } else if (z.right == null) {
            // no right child, promote left child into z's spot
            transplant(z, z.left);
        } else {
            // z has two children
            Node<T> successor = minimum(z.right); // find in-order successor

            if (successor.parent != z) {
                transplant(successor, successor.right);
                successor.right = z.right;
                successor.right.parent = successor;
            }
            // For both 3a and 3b: put successor in z's position
            transplant(z, successor);
            // Give the successor z's left subtree
            successor.left = z.left;
            successor.left.parent = successor;
        }
    }

    /**
     * TRANSPLANT (private helper)
     * Replaces node U with node V by rewiring U's parent to point to V instead, does NOT touch any children.
     * transplant(30, 20) on a tree → 50's left now points to 20, 20's parent now points to 50

     *   1. If u had no parent, it was the root → make v the new root.
     *   2. If u was its parent's LEFT child  → set parent's left  to v.
     *   3. If u was its parent's RIGHT child → set parent's right to v.
     *   4. If v is not null, update v's parent pointer to u's old parent.
     */
    private void transplant(Node<T> u, Node<T> v) {
        if (u.parent == null) {
            root = v; // u was the root, so v becomes the new root
        } else if (u == u.parent.left) {
            u.parent.left = v;  // u was a left child
        } else {
            u.parent.right = v; // u was a right child
        }
        if (v != null) {
            v.parent = u.parent; // update v's parent pointer
        }
    }
}

