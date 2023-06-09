import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Bipartite Graphs API is a simple and lightweight API for detecting a bipartite graph from a desired adjacency matrix loaded from file.
 *
 * No dependencies required, can be loaded in any Java 8+ environment.
 *
 * @author DenisMasterHerobrine (Denis Kalashnikov)
 * @version 1.0.1
 */

class BipartiteGraphsAPI {
    static int[][] MatrixHolder;

    /**
     * <p>
     *     This is a main method to be loaded in any testing enviroment to pass the checks.
     *     Also it does all the loading functionality to work with any processing data.
     * </p>
     * @param args Arguments of the Bipartite Graphs API, not supported in a testing environment.
     * @return type of the graph and it's cycle if not bipartite
     * @since 1.0.0
     */
    public static void main(String[] args) throws IOException {
        getData("input.txt");

        List<Integer>[] bipartitePartitions = getBipartitePartitions(MatrixHolder);
        List<Integer> cycle = null;

        if (bipartitePartitions == null) {
            cycle = findOddCycle(MatrixHolder);

            if (cycle != null) {
                Collections.reverse(cycle);

                // Iterate through the cycle and add 1 to each element to fit test's requirements.
                int iterator = 0;
                for (Integer element : cycle) {
                    cycle.set(iterator, element + 1);
                    iterator++;
                }
            }
        }

        setData(bipartitePartitions, cycle);
    }

    /**
     * Parses all processing data from the input.txt file.
     * 
     * This method reads a file given by its path, parses its contents and returns a 2D integer array. 
     * The first line of the file is read as an integer that represents the number of points to be processed. 
     * The remaining lines of the file are read as arrays of integers and stored in a matrix. 
     * If the file doesn't exist, an IOException is thrown.
     * 
     * @param path A path to the input data file, generated by the testing environment or the user. If not exists, passes as null.
     * @throws IOException if the file doesn't exist.
     * @since 1.0.0
     */
    public static void getData(String path) throws IOException {
        String line;
        int row = 0;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        int points = Integer.parseInt(bufferedReader.readLine());

        // Initialize 2D integer array to hold parsed data
        MatrixHolder = new int[points][points];

        // Loop through file lines and split into integer arrays
        while ((line = bufferedReader.readLine()) != null) {
            String[] rowArray = line.split(" ");
            for (int i = 0; i < rowArray.length; i++) {
                MatrixHolder[row][i] = Integer.parseInt(rowArray[i]);
            }

            row++;
        }
        bufferedReader.close();
    }

    /**
     * Writes the data of a bipartite graph to a "output.txt" file or writes "NOT BIPARTITE" and an odd cycle if a graph is not bipartite.
     *
     * @param bipartitePartition an array of two lists containing the vertices of the bipartite graph partitioned into two sets
     * @param cycle a list containing the vertices of an odd cycle in the graph if the graph is not bipartite
     */
    public static void setData(List<Integer>[] bipartitePartition, List<Integer> cycle) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

            if (bipartitePartition != null) {
                // Write first colored vertices.
                StringBuilder builder = new StringBuilder();

                for (Integer id : bipartitePartition[0]) {
                    builder.append(id);
                    builder.append(" ");
                }

                String string = builder.toString();
                // Remove last space.
                string = string.substring(0, string.length() - " ".length());

                writer.write(string);
                writer.newLine();

                // Write second colored vertices.
                builder = new StringBuilder();

                for (Integer id : bipartitePartition[1]) {
                    builder.append(id);
                    builder.append(" ");
                }

                string = builder.toString();
                // Remove last space.
                string = string.substring(0, string.length() - " ".length());

                writer.write(string);
            } else {
                writer.write("NOT BIPARTITE");
                writer.newLine();

                // Write an odd cycle.
                StringBuilder builder = new StringBuilder();

                for (Integer id : cycle) {
                    builder.append(id);
                    builder.append(" ");
                }

                String string = builder.toString();
                // Remove last space.
                string = string.substring(0, string.length() - " ".length());

                writer.write(string);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     *      This method performs a depth-first search of the graph and recolors its points.
     * </p>
     * @param MatrixHolder The matrix of the graph to be processed.
     * @param vertex The vertex to start the search from.
     * @param colors An array of colors assigned to each vertex.
     * @param color The color to assign to the current vertex.
     * @return A boolean indicating whether the graph is bipartite or not.
     * @since 1.0.0
     */
    private static boolean deepFirstSearchForPartitions(int[][] MatrixHolder, int vertex, int[] colors, int color) {
        // Assign the current color to the current vertex
        colors[vertex] = color;

        // Iterate over all vertices in the graph
        for (int i = 0; i < MatrixHolder.length; i++) {
            // If the current vertex is connected to the current vertex and the adjacent vertex has not been colored yet
            if (MatrixHolder[vertex][i] == 1) {
                if (colors[i] == -1) {
                    // Recursively call deepFirstSearchForPartitions with the adjacent vertex and the opposite color
                    if (!deepFirstSearchForPartitions(MatrixHolder, i, colors, 1 - color)) {
                        return false;
                    }
                } else if (colors[i] == color) {
                    // If the adjacent vertex has the same color as the current vertex, the graph is not bipartite
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines whether the given graph represented as an adjacency matrix is bipartite.
     * Returns the IDs of the vertices of each color in the bipartite partition, or null if the
     * graph is not bipartite.
     *
     * @param MatrixHolder the adjacency matrix representing the graph
     * @return an array containing lists of the IDs of the vertices of each color in the bipartite
     *         partition, or null if the graph is not bipartite
     * @since  1.0.0
     */
    public static List<Integer>[] getBipartitePartitions(int[][] MatrixHolder) {
        // Initialize array to keep track of colors for each vertex.
        // -1 represents a vertex that has not yet been colored.
        int[] colors = new int[MatrixHolder.length];
        Arrays.fill(colors, -1);

        // Check each vertex and its neighbors to see if the graph is bipartite.
        for (int i = 0; i < MatrixHolder.length; i++) {
            if (colors[i] == -1 && !deepFirstSearchForPartitions(MatrixHolder, i, colors, 1)) {
                return null;
            }
        }

        // Create lists of vertices for each color.
        List<Integer> verticesA = new ArrayList<>();
        List<Integer> verticesB = new ArrayList<>();
        for (int i = 0; i < MatrixHolder.length; i++) {
            if (colors[i] == 0) {
                verticesB.add(i + 1);
            } else {
                verticesA.add(i + 1);
            }
        }

        // Return the lists of vertices for each color.
        return new List[]{verticesA, verticesB};
    }

    /**
     * Finds an odd cycle in an undirected graph using DFS.
     *
     * @param MatrixHolder the adjacency matrix of the graph
     * @return a list of vertices in the odd cycle, or null if not found
     * @since 1.0.1
     */
    public static List<Integer> findOddCycle(int[][] MatrixHolder) {
        int length = MatrixHolder.length;
        boolean[] visited = new boolean[length];
        int[] parent = new int[length];
        Arrays.fill(parent, -1);

        // Traverse through all vertices of the graph
        for (int i = 0; i < length; i++) {
            if (!visited[i]) {
                List<Integer> cycle = deepFirstSearchForCycle(MatrixHolder, i, parent, visited);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        return null;
    }

    /**
     * Performs a depth-first search on a given node in an adjacency matrix
     * to detect cycles. If a cycle is detected, returns a list representing 
     * the nodes in the cycle. 
     *
     * @param MatrixHolder the adjacency matrix representing the graph
     * @param node the starting node to perform DFS on
     * @param parent an array representing the parent of each node in the DFS tree
     * @param visited an array representing whether each node has been visited
     * @return a list of nodes in the cycle, or null if no cycle is detected
     * @since 1.0.1
     */
    private static List<Integer> deepFirstSearchForCycle(int[][] MatrixHolder, int node, int[] parent, boolean[] visited) {
        visited[node] = true;

         // Looping through each neighbor of the node
        for (int neighbor = 0; neighbor < MatrixHolder.length; neighbor++) {

            // If there is an edge between the node and its neighbor
            if (MatrixHolder[node][neighbor] == 1) {

                // If the neighbor has not been visited yet, perform DFS on it
                if (!visited[neighbor]) {
                    parent[neighbor] = node;
                    List<Integer> cycle = deepFirstSearchForCycle(MatrixHolder, neighbor, parent, visited);
                    if (cycle != null) {
                        return cycle;
                    }
                }

                // If the neighbor has already been visited and it's not the parent of the node
                else if (parent[node] != neighbor) {

                    // Woo-hoo! We're found an odd cycle! Go iterate through it and return a list.
                    List<Integer> cycle = new ArrayList<>();
                    cycle.add(node);

                    int cur = parent[node];

                    while (cur != neighbor) {
                        cycle.add(cur);
                        cur = parent[cur];
                    }

                    cycle.add(neighbor);
                    return cycle;
                }
            }
        }

        // If we didn't found any, return null.
        return null;
    }
}