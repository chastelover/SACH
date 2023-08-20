package code.index;
import java.util.*;

public class RestComponent {
    public static List<Set<Integer>> getConnectedComponents(Map<Integer, int[]> graph, Set<Integer> deletedVertices, int k) {
        List<Set<Integer>> connectedComponents = new ArrayList<>();
        Set<Integer> remainingVertices = new HashSet<>(graph.keySet());
        remainingVertices.removeAll(deletedVertices);

        // 遍历剩下的顶点
        for (int vertex : remainingVertices) {
            // 如果该顶点还未被遍历到，并且满足度约束，则开始一个新的连通分量
            if (!isVertexVisited(connectedComponents, vertex)) {
                Set<Integer> component = new HashSet<>();
                dfs(graph, vertex, component, deletedVertices, k);
                if (component.size() >= k) {
                    connectedComponents.add(component);
                }
            }
        }

        return connectedComponents;
    }

    // 深度优先搜索遍历图
    private static void dfs(Map<Integer, int[]> graph, int vertex, Set<Integer> component, Set<Integer> deletedVertices, int k) {
        component.add(vertex);
        // FIXME 16:20
        int[] neighbors = graph.get(vertex);
        if (neighbors.length < k) {
            return;
        }
        if (neighbors != null) {
            for (int neighbor : neighbors) {
                if (!deletedVertices.contains(neighbor) && !component.contains(neighbor)) {
                    int[] neighborNeighbors = graph.get(neighbor);
                    int neighborDegree = countValidNeighbors(neighborNeighbors, deletedVertices);
                    if (neighborDegree >= k) {
                        dfs(graph, neighbor, component, deletedVertices, k);
                    }
                }
            }
        }
    }

    // 计算有效的邻居数量
    private static int countValidNeighbors(int[] neighbors, Set<Integer> deletedVertices) {
        int count = 0;
        for (int neighbor : neighbors) {
            if (!deletedVertices.contains(neighbor)) {
                count++;
            }
        }
        return count;
    }

    // 检查顶点是否已经被遍历到
    private static boolean isVertexVisited(List<Set<Integer>> connectedComponents, int vertex) {
        for (Set<Integer> component : connectedComponents) {
            if (component.contains(vertex)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Map<Integer, int[]> graph = new HashMap<>();
        // 假设graph已经填充了顶点之间的连通关系
        graph.put(0, new int[]{1,2,3,4,5});
        graph.put(1, new int[]{0,2,3,4,5});
        graph.put(2, new int[]{0,1,3,4,5});
        graph.put(3, new int[]{0,1,2,4,5});
        graph.put(4, new int[]{0,1,2,3,5});
        graph.put(5, new int[]{0,1,2,3,4});
        graph.put(7, new int[]{8});
        graph.put(8, new int[]{7});
        graph.put(9, new int[]{10,11,12});
        graph.put(10, new int[]{9, 11, 12});
        graph.put(11, new int[]{9, 10, 12});
        graph.put(12, new int[]{9, 10, 11});

        // 要删除的顶点集合
        Set<Integer> deletedVertices = new HashSet<>();
        deletedVertices.add(2);
        deletedVertices.add(4);
        deletedVertices.add(10);

        // 设置度约束
        int k = 3;
        List<Set<Integer>> connectedComponents = getConnectedComponents(graph, deletedVertices, k);

        for (int i = 0; i < connectedComponents.size(); i++) {
            Set<Integer> component = connectedComponents.get(i);
            System.out.println("Connected Component " + (i + 1) + ": " + component);
        }
    }
}
