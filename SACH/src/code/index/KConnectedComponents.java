package code.index;

import code.index.basic.BCoreDecomposition;
import code.online.MetaPath;
import code.util.DataReader;

import java.util.*;

import static code.index.TestIACHIndex.getConfig;

public class KConnectedComponents {

    /*
    public static List<Map<Integer, int[]>> getKConnectedComponents(Map<Integer, int[]> graph, int k) {
        List<Map<Integer, int[]>> kConnectedComponents = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        Map<Integer, List<Integer>> adjacencyMap = new HashMap<>();
        for (int vertex : graph.keySet()) {
            int[] neighbors = graph.get(vertex);
            adjacencyMap.put(vertex, new ArrayList<>(neighbors.length));
            for (int neighbor : neighbors) {
                adjacencyMap.get(vertex).add(neighbor);
            }
        }

        while (!adjacencyMap.isEmpty()) {
            int vertex = findUnvisitedVertex(adjacencyMap, visited);
            if (vertex == -1) {
                break; // 没有未访问的顶点，退出循环
            }
            List<Integer> connectedVertices = new ArrayList<>();
            Set<Integer> removedVertices = new HashSet<>();
            dfs(adjacencyMap, vertex, k, visited, connectedVertices, removedVertices);
            List<Integer> finalConnectedVertices = new ArrayList<>();
            for (int element: connectedVertices){
                if (!removedVertices.contains(element)){
                    finalConnectedVertices.add(element);
                }
            }
            if (finalConnectedVertices.size() > k) {
                Map<Integer, int[]> connectedComponent = new HashMap<>();
                connectedComponent.put(k, finalConnectedVertices.stream().mapToInt(Integer::intValue).toArray());
                kConnectedComponents.add(connectedComponent);
            }
            for (int removedVertex : removedVertices) {
                adjacencyMap.remove(removedVertex);
            }
        }

        return kConnectedComponents;
    }*/

    public static List<Map<Integer, int[]>> getKConnectedComponents(Map<Integer, int[]> graph, int k) {
        List<Map<Integer, int[]>> kConnectedComponents = new ArrayList<>();
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
                        // Check if the neighbor still exists in the graph before trying to access its list.
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

            // Add this connected component to the list.
            Map<Integer, int[]> connectedComponent = new HashMap<>();
            connectedComponent.put(k, connectedVertices.stream().mapToInt(Integer::intValue).toArray());
            kConnectedComponents.add(connectedComponent);

            // Remove the vertices of this connected component from the adjacency map.
            for (int vertex : connectedVertices) {
                adjacencyMap.remove(vertex);
            }
        }

        return kConnectedComponents;
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
//        Map<Integer, int[]> graph = new HashMap<>();
        // 假设graph已经填充了顶点之间的连通关系

        // ------------- G1 --------------
//        graph.put(0, new int[]{1,2,3,4,5});
//        graph.put(1, new int[]{0,2,3,4,5});
//        graph.put(2, new int[]{0,1,3,4,5});
//        graph.put(3, new int[]{0,1,2,4,5});
//        graph.put(4, new int[]{0,1,2,3,5});
//        graph.put(5, new int[]{0,1,2,3,4});
//        graph.put(7, new int[]{8});
//        graph.put(8, new int[]{7});
//        graph.put(9, new int[]{10,11,12});
//        graph.put(10, new int[]{9, 11, 12});
//        graph.put(11, new int[]{9, 10, 12});
//        graph.put(12, new int[]{9, 10, 11});

        // ------------ G2 --------------
//        graph.put(1, new int[]{2,3,4,5});
//        graph.put(2, new int[]{1,3});
//        graph.put(3, new int[]{1,2,4,5});
//        graph.put(4, new int[]{1,3,5});
//        graph.put(5, new int[]{1,3,4});
//        graph.put(6, new int[]{7});
//        graph.put(7, new int[]{6});

        // ---------- GPaper ---------
//        graph.put(1, new int[]{2,5,6,8});
//        graph.put(2, new int[]{1,3,4,5});
//        graph.put(3, new int[]{1,2,4,5});
//        graph.put(4, new int[]{2,3,5});
//        graph.put(5, new int[]{1,2,3,4});
//        graph.put(6, new int[]{1,7,8,9});
//        graph.put(7, new int[]{6,8,9,10});
//        graph.put(8, new int[]{1,6,7,10});
//        graph.put(9, new int[]{6,7,10,11});
//        graph.put(10, new int[]{7,8,9,12});
//        graph.put(11, new int[]{9,12});
//        graph.put(12, new int[]{10,11});
//
//        graph.put(13, new int[]{14,15});
//        graph.put(14, new int[]{13,15});
//        graph.put(15, new int[]{14,13});

         // ---------- G3 ---------
//        graph.put(1, new int[]{2});
//        graph.put(2, new int[]{1,3,4});
//        graph.put(3, new int[]{2,4});
//        graph.put(4, new int[]{2,3,5});
//        graph.put(5, new int[]{4,6,7,8});
//        graph.put(6, new int[]{5,7,8});
//        graph.put(7, new int[]{5,6,8});
//        graph.put(8, new int[]{5,6,7,9});
//        graph.put(9, new int[]{8,10,11,12,13});
//        graph.put(10, new int[]{9,11,12,13});
//        graph.put(11, new int[]{9,10,12,13});
//        graph.put(12, new int[]{9,10,11,13});
//        graph.put(13, new int[]{9,10,11,12});

        String type = "pubmed";
        DataReader dataReader = getConfig(type);
        int[][] graph = dataReader.readGraph();
        int[] vertexType = dataReader.readVertexType();
        int[] edgeType = dataReader.readEdgeType();
        double[] vertexWeight = dataReader.readVertexWeight();

        int[]vertex2 = {1, 0, 1};  // APA
        int[]edge2 = {3, 0};  // A->P, P->A
        MetaPath metaPath = new MetaPath(vertex2, edge2);

        BCoreDecomposition b = new BCoreDecomposition(graph, vertexType, edgeType);
        Map<Integer, int[]> pnbMap = b.decomposeAndGetGraph(metaPath);
        List<Map<Integer, int[]>> kConnectedComponents;
        for (int k = 49; k < 50; k++) {
            kConnectedComponents = getKConnectedComponents(pnbMap, k);
            List<Integer>all = new ArrayList<>();
            for (int i = 0; i < kConnectedComponents.size(); i++) {
                Map<Integer, int[]> connectedComponent = kConnectedComponents.get(i);
                System.out.println("Connected Component " + (i + 1) + ":");
                for (int key : connectedComponent.keySet()) {

                    System.out.println("k = " + key + ", vertices = " + Arrays.toString(connectedComponent.get(key)));
                    System.out.println(connectedComponent.get(key).length);
                    for (int element : connectedComponent.get(key)){
                        all.add(element);
                    }
                }

            }
            System.out.println(all.size());
        }

    }
}