# Java-graphs-binary-search-tree
Java implementations of graph data structures using adjacency matrix and adjacency list representations, alongside a generic binary search tree.

Files
FileDescriptionGraphAdjMatrix.javaGraph implemented using a 2D adjacency matrixGraphAdjList.javaGraph implemented using adjacency listsBinarySearchTreeImpl.javaGeneric binary search tree with full node operations

Features
Graphs (both implementations)

Directed and undirected weighted graphs
Add and remove edges
Get edge weight between two vertices
Get all neighbours of a vertex
Calculate vertex degree (in/out for directed graphs)
Check if a sequence of nodes forms a valid path
Count total number of edges

Binary Search Tree

Insert key-value pairs (updates value if key already exists)
Delete nodes (handles all three cases: leaf, one child, two children)
Search for a node by key
In-order traversal (returns values in sorted key order)
Find minimum and maximum keys
Find successor and predecessor of a given node
Calculate depth of a subtree


How to Compile and Run
Compile all files from the project root:
bashjavac GraphAdjMatrix.java GraphAdjList.java BinarySearchTreeImpl.java
Run a specific class (replace with your main class or test runner):
bashjava GraphAdjMatrix

Notes

Adjacency matrix uses Double.NaN to indicate the absence of an edge
The BST is generic (Node<T>) and can store any value type against an integer key
Both graph implementations support directed and undirected modes, set at construction time
