package code.index;

import code.index.basic.BCoreDecomposition;
import code.online.MetaPath;
import code.util.DataReader;

import java.util.*;

import static code.index.KConnectedComponents.getKConnectedComponents;
import static code.index.TestIACHIndex.getConfig;

public class KConnectedCompWithQuery {
    public static List<Set<Integer>> getKConnectedComponentContainingQuery(Map<Integer, int[]> graph, int k, int query) {
        List<Set<Integer>> result = new ArrayList<>();
        int minComponentValue = Integer.MAX_VALUE;

        Map<Integer, List<Integer>> adjacencyMap = new HashMap<>();

        // Initialize the adjacency map.
        for (int vertex : graph.keySet()) {
            int[] neighbors = graph.get(vertex);
            adjacencyMap.put(vertex, new ArrayList<>(neighbors.length));
            for (int neighbor : neighbors) {
                adjacencyMap.get(vertex).add(neighbor);
            }
        }

        // Keep iterating until there are no changes.
        boolean changesMade;
        do {
            changesMade = false;
            Iterator<Map.Entry<Integer, List<Integer>>> iterator = adjacencyMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, List<Integer>> entry = iterator.next();
                if (entry.getValue().size() < k) {
                    changesMade = true;
                    int vertexToRemove = entry.getKey();
                    // Make a copy of the neighbors' list so we don't modify it while iterating over it.
                    List<Integer> neighbors = new ArrayList<>(entry.getValue());
                    // Remove the vertex from the neighbors' lists.
                    for (int neighbor : neighbors) {
                        if (adjacencyMap.containsKey(neighbor)) {
                            adjacencyMap.get(neighbor).remove(Integer.valueOf(vertexToRemove));
                        }
                    }
                    // Remove the vertex from the graph.
                    iterator.remove();
                }
            }
        } while (changesMade);

        // The remaining vertices are part of a k-core.
        // Find the connected components within the k-core.
        while (!adjacencyMap.isEmpty()) {
            Set<Integer> visited = new HashSet<>();
            List<Integer> connectedVertices = new ArrayList<>();
            int startVertex = adjacencyMap.keySet().iterator().next();
            dfs(adjacencyMap, startVertex, k, visited, connectedVertices);

            // Check if the connected component contains the query vertex and if its min value is smaller than current min
            if (connectedVertices.contains(query)) {
                int minVal = Collections.min(connectedVertices);
                if (minVal < minComponentValue) {
                    minComponentValue = minVal;
                    result.clear();
                    result.add(new HashSet<>(connectedVertices));
                } else if (minVal == minComponentValue) {
                    result.add(new HashSet<>(connectedVertices));
                }
            }

            // Remove the vertices of this connected component from the adjacency map.
            for (int vertex : connectedVertices) {
                adjacencyMap.remove(vertex);
            }
        }

        return result;
    }

    private static void dfs(Map<Integer, List<Integer>> adjacencyMap, int vertex, int k, Set<Integer> visited, List<Integer> connectedVertices) {
        visited.add(vertex);
        connectedVertices.add(vertex);
        for (int neighbor : adjacencyMap.get(vertex)) {
            if (!visited.contains(neighbor)) {
                dfs(adjacencyMap, neighbor, k, visited, connectedVertices);
            }
        }
    }


    public static void main(String[] args) {
        int[]vertex2 = {1, 0, 1};  // AMA
        int[]edge2 = {1, 0};  // A->M, M->A
        MetaPath mp2 = new MetaPath(vertex2, edge2);

        int[]vertex1 = {1, 0, 2, 0, 1};  // AMDMA
        int[]edge1 = {1, 2, 3, 0};
        MetaPath mp1 = new MetaPath(vertex1, edge1);

        int[]vertex = {1, 0, 3, 0, 1}; // AMWMA
        int[]edge = {1, 4, 5, 0}; //A->M, M->W, W->M, M->A
        MetaPath mp = new MetaPath(vertex, edge);


        DataReader dataReader = getConfig("imdb");
        int[][] graph = dataReader.readGraph();
        int[] vertexType = dataReader.readVertexType();
        int[] edgeType = dataReader.readEdgeType();

        int[] queryKList = new int[]{9};
        int[] queryQList = new int[]{139551, 111495, 40002, 40001, 62081, 34306, 40003, 75373, 34307, 111496, 38413, 91603, 31888, 22387, 91604, 80624, 75374, 43702, 120949, 102812, 63292, 145502, 63293, 342131};

        BCoreDecomposition b = new BCoreDecomposition(graph, vertexType, edgeType);
        int queryID = 139551;
        int queryK = 1;
        System.out.println();
        Map<Integer, int[]> pnbMap = b.decomposeAndGetGraph(mp);
        List<Set<Integer>> KC = getKConnectedComponentContainingQuery(pnbMap, 1, 139551);
        for (Set<Integer> item:KC){
            System.out.println(item.size());
        }
    }
}
