package code.index;

import java.util.*;
import java.util.stream.IntStream;
// 用于计算 k-core 的类
public class KCoreFinder {
    private Map<Integer, int[]> adjacencyMap; // 邻接矩阵

    public KCoreFinder(Map<Integer, int[]> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }

    // 该函数输入一个 k 值，返回一个 map，其中 key 为节点，value 为该节点的度数，即一个（顶点->核数）的Map
    public Map<Integer, Integer> findKCore(int k) {
        Map<Integer, Integer> kCoreMap = new HashMap<>();
        Map<Integer, Integer> degreeMap = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        // 计算每个节点的度数
        for (int node : adjacencyMap.keySet()) {
            degreeMap.put(node, adjacencyMap.get(node).length);
        }

        while (!degreeMap.isEmpty()) {
            // 查找度数最小的节点
            Map.Entry<Integer, Integer> minEntry = Collections.min(degreeMap.entrySet(), Map.Entry.comparingByValue());

            int minDegreeNode = minEntry.getKey();
            int minDegree = minEntry.getValue();

            if (minDegree < k) {
                // 移除度数小于 k 的节点及其边
                int[] neighbors = adjacencyMap.get(minDegreeNode);
                for (int neighbor : neighbors) {
                    if (degreeMap.get(neighbor)!=null) {
                        int neighborDegree = degreeMap.get(neighbor);
                        degreeMap.put(neighbor, neighborDegree - 1);
                    }
                }
                degreeMap.remove(minDegreeNode);
                visited.add(minDegreeNode);
            } else {
                // 添加到 k-core
                kCoreMap.put(minDegreeNode, minDegree);
                visited.add(minDegreeNode);
            }
            degreeMap.remove(minDegreeNode);
        }

        // 将剩余的节点添加到 k-core
        for (int node : degreeMap.keySet()) {
            kCoreMap.put(node, degreeMap.get(node));
        }

        return kCoreMap;
    }

    public static void main(String[] args) {
        // 示例用法
        Map<Integer, int[]> adjacencyMap = new HashMap<>();
        adjacencyMap.put(0, new int[]{1, 2, 3});
        adjacencyMap.put(1, new int[]{0, 2, 3});
        adjacencyMap.put(2, new int[]{0,1,3,4});
        adjacencyMap.put(3, new int[]{0,1,2,4,5});
        adjacencyMap.put(4, new int[]{2,3,5,6});
        adjacencyMap.put(5, new int[]{3,4});
        adjacencyMap.put(6, new int[]{4});


        KCoreFinder kCoreFinder = new KCoreFinder(adjacencyMap);

        for (int k = 0; k < 10; k++){
            Map<Integer, Integer> kCoreMap = kCoreFinder.findKCore(k);
            if (kCoreMap.size() == 0) return;
            System.out.println("K-Core with k = " + k + ":");
            for (int node : kCoreMap.keySet()) {
                System.out.println("Node: " + node + ", Degree: " + kCoreMap.get(node));
            }
        }
    }
}
