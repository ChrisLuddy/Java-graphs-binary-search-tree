package assignment1;

import java.util.LinkedList;

public class GraphAdjList extends AbstractGraph {
    private record Edge(int destination, double weight) {}
    private final LinkedList<Edge>[] neighbours;


    /**
     * CONSTRUCTOR
     * Each vertex gets its own LinkedList to store its edges.
     * Unlike the matrix which has a full grid, this only stores edges that exist.
     *
     * neighbours[0] → []
     * neighbours[1] → []
     * neighbours[2] → []
     * 
     * 
     * creating an empty LinkedList for each vertex guarantees
     * no false edges exist at the start. Every real edge must be explicitly added.
     */
    public GraphAdjList(int noOfVertices, boolean directed) {
        super(noOfVertices, directed);

        // Initialize the array of LinkedLists
        neighbours = (LinkedList<Edge>[]) new LinkedList[noOfVertices];

        // Create a new LinkedList for each vertex
        for (int i = 0; i < noOfVertices; i++) {
            neighbours[i] = new LinkedList<>();
        }
    }


    /**
     * ADD EDGE
     * Adds an Edge object to the source's LinkedList.
     * Undirected = add both directions.
     *
     * addEdge(0, 1, 5.0) undirected:
     * neighbours[0] → [(dest=1, w=5.0)]
     * neighbours[1] → [(dest=0, w=5.0)]
     *
     * addEdge(0, 1, 5.0) directed:
     * neighbours[0] → [(dest=1, w=5.0)]   ← only source gets the edge
     * neighbours[1] → []                   ← destination stays empty
     * neighbours[2] → []
     * 
     * adding an Edge object to the source's list is the only
     * thing needed to record a directed connection.
     * For undirected, adding the reverse Edge to the destination's list ensures
     * both vertices can see the connection, which is required for undirected graphs.
     */

    public void addEdge(int source, int destination, double weight) {
        // Add edge from source to destination
        neighbours[source].add(new Edge(destination, weight));

        // If the graph is undirected, also add edge from destination to source
        if (!directed) {
            neighbours[destination].add(new Edge(source, weight));
        }
    }


    /**
     * REMOVE EDGE
     * Scans the source's LinkedList and removes the edge where destination matches.
     * Undirected = remove both directions.
     *
     * removeEdge(0, 1) undirected:
     * neighbours[0] → []
     * neighbours[1] → []
     * 
     * removeEdge(0, 1) directed:
     * neighbours[0] → []                 ← only source list is changed
     * neighbours[1] → [(dest=0, w=5.0)]  ← destination list untouched
     * 
     * removeIf scans the entire list and removes the exact edge
     * that matches the destination, which is the exact reverse of addEdge.
     * For undirected, removing both directions ensures no half-deleted edges
     * remain where one vertex still thinks the connection exists.
     */

    public void removeEdge(int source, int destination) {
        // Remove edge from source to destination
        neighbours[source].removeIf(edge -> edge.destination == destination);

        // If the graph is undirected, also remove edge from destination to source
        if (!directed) {
            neighbours[destination].removeIf(edge -> edge.destination == source);
        }
    }

    /**
     * GET WEIGHT
     * Example: getWeight(0, 1) on this graph:
     *      neighbours[0] → [(dest=1, w=5.0),   (dest=2, w=3.0)]
     *      Scan list: dest=1 matches! → return 5.0
     * 
     * scanning the source's list and matching the destination
     * is the only way to find an edge in an adjacency list since there is no
     * direct cell lookup like the matrix. Returning NaN when nothing is found
     * is consistent with the matrix version.
     */
    public double getWeight(int source, int destination) {
        // Search through the adjacency list of source vertex
        for (Edge edge : neighbours[source]) {
            if (edge.destination == destination) {
                return edge.weight;
            }
        }

        // Edge not found
        return Double.NaN;
    }

    /**
     * GET NEIGHBOURS
     * Reads the vertex's LinkedList and returns all destination values.
     *
     * neighbours[0] → [(dest=1, w=5.0), (dest=2, w=3.0)]
     * getNeighbours(0) → [1, 2]
     * 
     * every entry in the vertex's LinkedList is a real edge
     * that was explicitly added. Reading all destinations from the list
     * is guaranteed to return only real neighbours and nothing extra.
     */

    public int[] getNeighbours(int vertex) {
        // Get the size of the adjacency list
        int size = neighbours[vertex].size();

        // Create array and fill it with destination vertices
        int[] result = new int[size];
        int index = 0;
        for (Edge edge : neighbours[vertex]) {
            result[index++] = edge.destination;
        }

        return result;
    }

    /**
     * GET DEGREE
     * Undirected = just return the size of the vertex's LinkedList.
     * Directed = out-degree (size of own list) + in-degree (scan ALL lists for edges pointing TO this vertex).
     *
     * Undirected: neighbours[0] → [(dest=1), (dest=2)] → return 2
     * Directed:   neighbours[1] → [(dest=2)] → out=1
     *             scan all lists for dest=1 → found in neighbours[0] → in=1
     *             total = 2
     * 
     * for undirected graphs the list size is the degree since
     * every entry is a unique connection.
     * For directed graphs, the list size gives out-degree since each entry
     * is an outgoing edge. Scanning ALL other lists for edges pointing TO
     * this vertex correctly counts in-degree since that is the only place
     * incoming edges are stored. Adding both gives the complete degree.
     */

    public int getDegree(int vertex) {
        int degree = 0;

        if (directed) {
            // Out-degree: number of edges going out from vertex
            degree = neighbours[vertex].size();

            // In-degree: count edges coming into vertex from all other vertices
            for (int i = 0; i < noOfVertices; i++) {
                for (Edge edge : neighbours[i]) {
                    if (edge.destination == vertex) {
                        degree++;
                    }
                }
            }
        } else {
            // For undirected graphs, degree is just the size of adjacency list
            degree = neighbours[vertex].size();
        }

        return degree;
    }

    /**
     * IS PATH
     * Goes pair by pair and scans the current vertex's LinkedList to check if the next vertex exists.
     * One missing edge = false. All pairs found = true.
     *
     * isPath([0, 1, 2]):
     * Check 0→1: scan neighbours[0] → dest=1 found
     * Check 1→2: scan neighbours[1] → dest=2 found
     * return true!
     * 
     * checking every consecutive pair is both necessary and
     * sufficient. If any single pair has no edge the path is broken.
     * If every pair has an edge the path is valid end to end.
     * Scanning the LinkedList for each pair correctly finds whether that
     * specific edge exists since edges are stored by destination in each list.
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
            boolean edgeExists = false;
            for (Edge edge : neighbours[current]) {
                if (edge.destination == next) {
                    edgeExists = true;
                    break;
                }
            }

            if (!edgeExists) {
                return false;
            }
        }

        return true;
    }

    /**
     * GET NUMBER OF EDGES
     * Directed = add up ALL list sizes, each entry is a unique edge.
     * Undirected = add up all list sizes then DIVIDE BY 2 since each edge appears twice.
     *
     * Directed:   [2] + [1] + [0] = 3 edges
     * Undirected: [2] + [2] + [2] = 6 / 2 = 3 edges
     * 
     * 
     * for directed graphs every entry across all lists is a
     * unique edge since direction matters, so summing all list sizes is correct.
     * For undirected graphs each edge is stored twice, once in each direction,
     * so dividing the total by 2 gives exactly the right number of unique edges.
     */

    public int getNoOfEdges() {
        int count = 0;

        if (directed) {
            // For directed graphs, count all edges in all adjacency lists
            for (int i = 0; i < noOfVertices; i++) {
                count += neighbours[i].size();
            }
        } else {
            // For undirected graphs, count all edges and divide by 2
            // (since each edge appears in two adjacency lists)
            for (int i = 0; i < noOfVertices; i++) {
                count += neighbours[i].size();
            }
            count /= 2;
        }

        return count;
    }}