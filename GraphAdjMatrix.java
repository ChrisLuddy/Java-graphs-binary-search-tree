package assignment1;

public class GraphAdjMatrix extends AbstractGraph {
    private final double[][] adjMatrix;


    /**
     * CONSTRUCTOR create graph with number of vertices
     * (undirected A <———> B both ways : directed or  A ——→ B one way ).
     * A 2D grid (matrix) is used where adjMatrix[i][j] holds the weight of the edge
     * from vertex i to vertex j. If there is no edge, the cell holds Double.NaN.
     * All cells start as NaN meaning no edges exist yet.
     *
     * Example: new GraphAdjMatrix(3, false) creates this matrix:
     *
     *     0    1    2
     * 0 [NaN, NaN, NaN]
     * 1 [NaN, NaN, NaN]
     * 2 [NaN, NaN, NaN]
     * 
     * initializing all cells to NaN guarantees no false edges
     * exist at the start. Every real edge must be explicitly added.
     */
    public GraphAdjMatrix(int noOfVertices, boolean directed) {
        super(noOfVertices, directed);

        adjMatrix = new double[noOfVertices][noOfVertices];

        // Initialize all entries with Double.NaN to indicate no connection
        for (int i = 0; i < noOfVertices; i++) {
            for (int j = 0; j < noOfVertices; j++) {
                adjMatrix[i][j] = Double.NaN;
            }
        }
    }

    /**
     * ADD EDGE: Create a connection between two vertices with a given weight.
     * Sets adjMatrix[source][destination] = weight.
     * If the graph is undirected, also sets the reverse direction so both
     * vertices know about the connection.
     *
     * Example: addEdge(0, 1, 5.0) on an undirected 3-vertex graph:
     *
     *     0    1    2              0    1    2
     * 0 [NaN, NaN, NaN]   →   0 [NaN, 5.0, NaN]
     * 1 [NaN, NaN, NaN]       1 [5.0, NaN, NaN]
     * 2 [NaN, NaN, NaN]       2 [NaN, NaN, NaN]
     * 
     * setting adjMatrix[source][destination] directly encodes
     * the edge in the only cell that represents that direction.
     * For undirected, mirroring to adjMatrix[destination][source] ensures
     * both vertices can see the connection, which is required for undirected graphs.
     */


    public void addEdge(int source, int destination, double weight) {

        // Add edge from source to destination
        adjMatrix[source][destination] = weight;

        // If the graph is undirected, also add edge from destination to source
        if (!directed) {
            adjMatrix[destination][source] = weight;
        }
    }


    /**
     * REMOVE EDGE: Delete the connection between two vertices by setting the cell back to NaN.
     * If the graph is undirected, removes both directions of the connection.
     *
     * Example: removeEdge(0, 1) on an undirected graph with edge (0→1, weight 5.0):
     *
     *     0    1    2              0    1    2
     * 0 [NaN, 5.0, NaN]   →   0 [NaN, NaN, NaN]
     * 1 [5.0, NaN, NaN]       1 [NaN, NaN, NaN]
     * 2 [NaN, NaN, NaN]       2 [NaN, NaN, NaN]
     *
     * for directed u can row, column such as row 1 column 0 :
     *      0    1    2              0    1    2
     * 0 [NaN, 5.0, NaN]   →   0 [NaN, 5.0, NaN]
     * 1 [5.0, NaN, NaN]       1 [NaN, NaN, NaN]
     * 2 [NaN, NaN, NaN]       2 [NaN, NaN, NaN]
     * 
     * 
     * setting the cell back to NaN is the exact reverse of addEdge.
     * For undirected, removing both directions ensures no half-deleted edges remain
     * where one vertex still thinks the connection exists.
     */
    public void removeEdge(int source, int destination) {
        // Remove edge from source to destination
        adjMatrix[source][destination] = Double.NaN;

        // If the graph is undirected, also remove edge from destination to source
        if (!directed) {
            adjMatrix[destination][source] = Double.NaN;
        }
    }

    /**
     * GET WEIGHT
     * Goal: Return the weight of the edge between two vertices.
     * Simply reads the value directly from the matrix.
     * Returns Double.NaN if no edge exists between those two vertices.
     * adjMatrix[source][destination] is the single cell that
     * stores exactly this edge's weight. Reading it directly is always accurate.
     * NaN is returned naturally if no edge exists since all empty cells are NaN.
    */
    public double getWeight(int source, int destination) {
        return adjMatrix[source][destination];
    }

    /**
     * GET NEIGHBOURS:
     * scan row and return column no. that have values:
     *     0    1    2
       1 [8.0, NaN, 8.0]
            ↑         ↑
          col0       col2  ← these are what we return!
        so 0,2

     * scanning the entire row of a vertex finds every outgoing
     * edge since each column in that row represents a potential connection.
     * Only non-NaN cells are collected, guaranteeing only real edges are returned.
     * Two passes are needed because Java arrays need a fixed size upfront.
     */

    public int[] getNeighbours(int vertex) {
        // First, count how many neighbours exist
        int count = 0;
        for (int i = 0; i < noOfVertices; i++) {
            if (!Double.isNaN(adjMatrix[vertex][i])) {
                count++;
            }
        }

        // Create array of the right size and fill it
        int[] neighbours = new int[count];
        int index = 0;
        for (int i = 0; i < noOfVertices; i++) {
            if (!Double.isNaN(adjMatrix[vertex][i])) {
                neighbours[index++] = i;
            }
        }

        return neighbours;
    }


    /**
     * GET DEGREE:
     * scan row and return how many edges are connected to it
     *      0    1    2
       1 [8.0, NaN, 8.0]
            ↑         ↑
            1         1      ← these are what we return!
        1+1 = 2
     * for undirected graphs each edge in the row is a unique
     * connection so counting the row is sufficient.
     * For directed graphs, scanning the row counts edges LEAVING the vertex
     * and scanning the column counts edges ARRIVING at the vertex.
     * Adding both together gives the complete degree as required.
     */

    public int getDegree(int vertex) {
        int degree = 0;

        if (directed) {
            // For directed graphs, degree = in-degree + out-degree

            // Out-degree: count edges going out from vertex
            for (int i = 0; i < noOfVertices; i++) {
                if (!Double.isNaN(adjMatrix[vertex][i])) {
                    degree++;
                }
            }

            // In-degree: count edges coming into vertex
            for (int i = 0; i < noOfVertices; i++) {
                if (!Double.isNaN(adjMatrix[i][vertex])) {
                    degree++;
                }
            }
        } else {
            // For undirected graphs, just count neighbours
            for (int i = 0; i < noOfVertices; i++) {
                if (!Double.isNaN(adjMatrix[vertex][i])) {
                    degree++;
                }
            }
        }

        return degree;
    }



    /**
     * IS PATH: Check if valid path has consecutive real pair of vertices in the array (not NaN).
     *
     * Example: graph with edges (0→1) and (1→2):
     *
     *      0    1    2
        0 [NaN, 5.0, NaN]
        1 [NaN, NaN, 3.0]
        2 [NaN, NaN, 4.0]
     *
     *   isPath([0, 1, 2])
     * break it up!
     *   isPath([0, 1])    → true   5.0
     *   isPath([1, 2])    → true   3.0
     * 
     * checking every consecutive pair is both necessary and
     * sufficient. If any single pair has no edge the path is broken.
     * If every pair has an edge the path is valid end to end.
     * A single node is trivially a valid path since no edges need to be checked.
     */

    public boolean isPath(int[] nodes) {
        // Empty or single node is considered a valid path
        if (nodes.length <= 1) {
            return true;
        }

        // Check if consecutive nodes are connected
        for (int i = 0; i < nodes.length - 1; i++) {
            int current = nodes[i];
            int next = nodes[i + 1];

            // Check if there's an edge from current to next
            if (Double.isNaN(adjMatrix[current][next])) {
                return false;
            }
        }

        return true;
    }

    /**
     * GET NUMBER OF EDGES:
     * DIRECTED — scan all
     * UNDIRECTED — only scan the upper triangle (where j >= i) to avoid counting each edge twice since every edge is stored in two cells.
     * for directed graphs every non-NaN cell is a unique edge
     * since direction matters, so counting all cells is correct.
     * For undirected graphs each edge is stored twice (both directions)
     * so scanning only the upper triangle where j >= i counts each edge
     * exactly once, giving the correct total.
     */


    public int getNoOfEdges() {
        int count = 0;

        if (directed) {
            // For directed graphs, count all non-NaN entries
            for (int i = 0; i < noOfVertices; i++) {
                for (int j = 0; j < noOfVertices; j++) {
                    if (!Double.isNaN(adjMatrix[i][j])) {
                        count++;
                    }
                }
            }
        } else {
            // For undirected graphs, count upper triangle only (to avoid double counting)
            for (int i = 0; i < noOfVertices; i++) {
                for (int j = i; j < noOfVertices; j++) {
                    if (!Double.isNaN(adjMatrix[i][j])) {
                        count++;
                    }
                }
            }
        }

        return count;
    }
}