package code.index.basic;

import code.MyException;
import code.index.*;
import code.index.tree.*;
import code.online.MetaPath;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;

import static code.index.RestComponent.getConnectedComponents;
import static code.index.tree.IndexKNode.processAdvancedKNode;
import static code.index.tree.IndexKTree.printIndexKTree;


public class ConstructIACHIndex {
    private int[][] graph = null;//data graph, including vertex IDs, edge IDs, and their link relationships
    private int[] vertexType = null;//vertex -> type
    private int[] edgeType = null;//edge -> type
    private double[] weight = null;//vertex -> weight
    private List<MetaPath> pathList = null;//a list of meta-paths

    public double[] getWeight() {
        return weight;
    }

    public ConstructIACHIndex(double[] weight){
        this.weight=weight;
    }

    public ConstructIACHIndex(int[][] graph, int[] vertexType, int[] edgeType, double[] weight, List<MetaPath> metaPathList) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.weight = weight;
        this.pathList = metaPathList;
    }

    public IndexTree buildIndex(String fileName, String prefix, int forTest) throws Exception {
        long startTime = System.nanoTime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
//        System.out.println("最大堆内存：" + heapMemoryUsage.getMax() / 1024 / 1024 / 1024 + " GiB");
//        System.out.println("Building Index");

        IndexTreePrinter indexTreePrinter = new IndexTreePrinter(fileName);
//        FileWriter fileWriter = new FileWriter(fileName);
//        PrintWriter writer = new PrintWriter(fileWriter, true); // 设置自动刷新

        if (pathList != null){
            indexTreePrinter.println("-----------------------------IndexTreeRoot--------------------------");
        }
        //step1: 初始化一级索引即元路径节点
        IndexTree indexTree = new IndexTree();
        for(MetaPath metaPath : pathList){
            IndexMPNode indexMPNode = new IndexMPNode(metaPath);
            indexTree.addChild(indexMPNode);
        }

        //step2: 初始化二级索引, 为每个元路径节点构造 k 个子节点

        for (IndexMPNode indexMPNode: indexTree.getChildren()){
            // 调用核分解，获取最大核数
            indexTreePrinter.println(prefix + "└──── IndexMPNode: " + indexMPNode.getMetaPath().printVertexName());
            BCoreDecomposition b = new BCoreDecomposition(graph, vertexType, edgeType);
            Map<Integer, int[]> pnbMap = b.decomposeAndGetGraph(indexMPNode.getMetaPath());
//            KCoreFinder kCoreFinder = new KCoreFinder(pnbMap);
//            HashMap<HashSet<Integer>, HashSet<Integer>> indexTree3 = new HashMap<>();
            if (forTest >= b.getMaxK()) forTest=b.getMaxK();
            System.out.println(forTest);
//            for (int i = forTest - 5; i <= forTest; i++) {  //fixme
            for (int i = forTest; i <= forTest; i++){
                if (i <= 0){
                    continue;
                }
//            int i = 49;
                IndexKNode indexKNode = null;
                List<Map<Integer, int[]>> kCores = KConnectedComponents.getKConnectedComponents(pnbMap, i);
                for (Map<Integer, int[]> kCore : kCores) {
                    for (int[] component: kCore.values()){
                        if (indexKNode == null) {
                            indexKNode = new IndexKNode(i, indexMPNode);
                            indexMPNode.addKNodeChild(indexKNode);
                        }
                         // TODO 等写索引优化的时候，这里可以只处理在kCore中且不在kCore+1中的节点
                         createIACHIndexTree(component, indexKNode, null, i, pnbMap, startTime);

                            // 在处理小数据集时可以不用clear
                    }
                }
                indexTreePrinter.printIndexKTree(prefix + "     ", indexKNode, false);
                printIndexKTree(prefix, indexKNode, false);
                assert indexKNode != null;
                indexKNode.getChildren().clear();
                // FIXME 这里clear了会在最后return的indexTree上不完整，但由于内存占用不得不这样
            }
        }
        indexTreePrinter.println("-----------------------------IndexTreeRoot--------------------------");
        return indexTree;
    }

    public IndexTree buildAdvanced(String fileName, String prefix) throws Exception {
        long startTime = System.nanoTime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
//        System.out.println("最大堆内存：" + heapMemoryUsage.getMax() / 1024 / 1024 / 1024 + " GiB");
//        System.out.println("Building Index");

        IndexTreePrinter indexTreePrinter = new IndexTreePrinter(fileName);
//        FileWriter fileWriter = new FileWriter(fileName);
//        PrintWriter writer = new PrintWriter(fileWriter, true); // 设置自动刷新

        if (pathList != null){
            indexTreePrinter.println("-----------------------------IndexTreeRoot--------------------------");
        }
        //step1: 初始化一级索引即元路径节点
        IndexTree indexTree = new IndexTree();
        for(MetaPath metaPath : pathList){
            IndexMPNode indexMPNode = new IndexMPNode(metaPath);
            indexTree.addChild(indexMPNode);
        }

        //step2: 初始化二级索引, 为每个元路径节点构造 k 个子节点
        for (IndexMPNode indexMPNode: indexTree.getChildren()) {
            // 调用核分解，获取最大核数
            indexTreePrinter.println(prefix + "└──── IndexMPNode: " + indexMPNode.getMetaPath().printVertexName());
            BCoreDecomposition b = new BCoreDecomposition(graph, vertexType, edgeType);
            Map<Integer, int[]> pnbMap = b.decomposeAndGetGraph(indexMPNode.getMetaPath());
            IndexKNode indexKNodeLast = null;
            IndexKNode indexKNode = null;
            for (int i = 1; i <= b.getMaxK(); i++) {
                indexKNodeLast = indexKNode;
                indexKNode = new IndexKNode(i, indexMPNode);
                indexMPNode.addKNodeChild(indexKNode);
                List<Map<Integer, int[]>> kCores = KConnectedComponents.getKConnectedComponents(pnbMap, i);
                if (kCores.size() == 0) {
                    throw new MyException("getKConnectedComponents失败");
                }
                for (Map<Integer, int[]> kCore : kCores) {
                    for (int[] component : kCore.values()) {
                        createIACHIndexTree(component, indexKNode, null, i, pnbMap, startTime);
                    }
//                    indexKNode.getChildren().clear();
                }
                if (indexKNodeLast == null) {
                    continue;
                }
                indexKNode.setDirectVertices();
                indexKNode.calVertices();
//            printIndexKTree(prefix, indexKNode, false); /

                processAdvancedKNode(indexKNodeLast, indexKNode);
                indexKNodeLast.calObVertices();
                indexMPNode.setKVerticesMap(indexKNodeLast.getK(), indexKNodeLast.getObVertices());
                indexTreePrinter.printIndexKTree(prefix + "     ", indexKNodeLast, true);
                printIndexKTree(prefix, indexKNodeLast, true);

                indexKNodeLast.getChildren().clear();
            }
            assert indexKNode != null;
            indexKNode.calObVertices();
            indexMPNode.setKVerticesMap(indexKNode.getK(), indexKNode.getObVertices());
            indexTreePrinter.printIndexKTree(prefix + "     ", indexKNode, true);
//
            printIndexKTree(prefix, indexKNode, true);
        }
        return indexTree;
    }

    private Map<Integer,int[]> buildNewMap(int[] component, Map<Integer,int[]> pnbMap) {
        Map<Integer,int[]> newPnbMap = new HashMap<>();
        Set<Integer>componentList = new HashSet<>();
        for (int j : component) {
            componentList.add(j);
        }
        for (int vertex: component){
            List<Integer>neighborsList = new ArrayList<>();
            for (int v : pnbMap.get(vertex)){
                if (componentList.contains(v)){
                    neighborsList.add(v);
                }
            }
            int index = 0;
            int[] neighborsArray = new int[neighborsList.size()];
            for (Integer neighbor: neighborsList){
                neighborsArray[index] = neighbor;
                index++;
            }
            newPnbMap.put(vertex, neighborsArray);
        }

        return newPnbMap;
    }

    private void createIACHIndexTree(int[] component, IndexKNode indexKNode, IndexTreeNode indexTreeNode, int k, Map<Integer, int[]>pnbMap, long tempTime) throws Exception {
        // 如果KCore为空直接返回
        long nowTime = System.nanoTime() - tempTime;
        double durTime = (double)nowTime / 1_000_000_000.0;
        if (durTime % 60 == 0) {
            System.out.println("=================" + durTime / 60 + "minutes====================");
        }
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
//        System.out.println("剩余内存：" + freeMemory / 1024 / 1024 / 1024 + " GiB");
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
//        System.out.println("已用堆内存：" + heapMemoryUsage.getUsed() / 1024 / 1024 / 1024 + " GiB");
        if (component == null || component.length == 0) {
            return;
        }
        Map<Integer, int[]>newPnbMap = buildNewMap(component, pnbMap);
        // 将C中属性值最小的顶点形成一个集合作为Temp，集合要能弹出顶点
        GraphComponent graphComponent = new GraphComponent(component, k);
        // 返回具有最小属性值的顶点集合
        List<Integer> minKeys = graphComponent.getVerticesWithMinAttribute(component, this);
        Queue<Integer> temp = new LinkedList<>(minKeys);
        // 初始化一个空集名为Delete
        HashSet<Integer> delete = new HashSet<>();
        HashMap<Integer, int[]>pnbMapCopy = new HashMap<>(newPnbMap);
        HashSet<Integer> reserve = new HashSet<>();
//        List<Integer>deleteArrayList = new ArrayList<>();
        // 使用一个while循环，当Temp不为空时，执行以下操作
        while (!temp.isEmpty()) {
            int cur = temp.poll();
            int[] neighbors = pnbMapCopy.get(cur);
            if (neighbors != null){
                for (Integer neighbor : neighbors) {
//                HashMap<Integer, Integer>degreeMap = new HashMap<>();
                    //如果删除了这个点，如果小于了k，那么这个点不能删除，加入到temp中

                    int degree = pnbMapCopy.get(neighbor).length - 1;
                    if (degree < k) {
                        reserve.add(cur);
                        if (!temp.contains(neighbor)) {
                            if (!delete.contains(neighbor)) {
                                temp.add(neighbor);
                            }
                        }
                    }
                }
            }


            // 从component中删除顶点cur
            delete.add(cur);
            if (!temp.contains(cur)){
                removeElementFromHashMap1(cur, pnbMapCopy);reserve.remove(cur);
            }
        }
        removeElementsFromHashMap1(reserve, pnbMapCopy);
        int[] deleteArray = new int[delete.size()];
        int indexOfDelete = 0;
        for (Integer vertex : delete) {
            deleteArray[indexOfDelete] = vertex;
            indexOfDelete++;
        }
        if (indexTreeNode == null){
            indexTreeNode = new IndexTreeNode(deleteArray, this);
        }
        if (indexTreeNode.getVertexSet().size() == 0 || indexTreeNode.getVertexSet() == null){
            indexTreeNode.setVertexSet(deleteArray, this);
            indexTreeNode.setParentChild();
        }
        if (indexTreeNode.getKParent() == null){
            indexTreeNode.setKParent(indexKNode);
        }

        // 判断连通分量中删除一个点后的连通情况
        List<Set<Integer>> connectedComponents = getConnectedComponents(pnbMapCopy, delete, k);
        for (Set<Integer> connectedComponent: connectedComponents){
//            System.out.println(connectedComponent);
            int index = 0;
            int[] connectedComponentArray = new int[connectedComponent.size()];
            for (Integer vertex: connectedComponent){
                connectedComponentArray[index] = vertex;
                index++;
            }

            IndexTreeNode indexTreeNode1 = new IndexTreeNode(indexTreeNode, indexKNode);
            //
            createIACHIndexTree(connectedComponentArray, indexKNode, indexTreeNode1, k, pnbMapCopy, tempTime);
//            System.out.println(connectedComponent);
        }
        indexKNode.addChildKNode(indexTreeNode);


    }

    private List<Map<Integer,int[]>> getKCores(Map<Integer,int[]> pnbMap, int k) {
        Queue<Integer> queue = new LinkedList<Integer>();
        Set<Integer> deleteSet = new HashSet<Integer>();
        for(Map.Entry<Integer, int[]> entry : pnbMap.entrySet()) {
            int curId = entry.getKey();
            int[] pnbSet = entry.getValue();
            if(pnbSet.length < k) {
                queue.add(curId);
                deleteSet.add(curId);
            }
        }

        while(queue.size() > 0) {
            int curId = queue.poll();//delete curId
            int[] pnbSet = pnbMap.get(curId);
            for(int pnb:pnbSet) {//update curId's pnb
                if(!deleteSet.contains(pnb)) {
                    int[] tmpSet = pnbMap.get(pnb);
                    // 从 pnbSet 中删除 curId
                    tmpSet = removeElement(tmpSet, curId);
                    if(tmpSet.length < k) {
                        queue.add(pnb);
                        deleteSet.add(pnb);
                    }
                }
            }
            pnbMap.remove(curId); //删除当前顶点
        }

        return getConnectedSubGraphs(pnbMap);
    }

    private List<Map<Integer,int[]>> getConnectedSubGraphs(Map<Integer,int[]> pnbMap) {
        List<Map<Integer,int[]>> connectedSubGraphs = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        for (Map.Entry<Integer, int[]> entry : pnbMap.entrySet()) {
            int vertex = entry.getKey();
            if (!visited.contains(vertex)) {
                Map<Integer, int[]> subGraph = new HashMap<>();
                dfs(pnbMap, vertex, visited, subGraph);
                connectedSubGraphs.add(subGraph);
            }
        }
        return connectedSubGraphs;
    }

    private void dfs(Map<Integer,int[]> pnbMap, int vertex, Set<Integer> visited, Map<Integer,int[]> subGraph) {
        visited.add(vertex);
        subGraph.put(vertex, pnbMap.get(vertex));
        for (int neighbor : pnbMap.get(vertex)) {
            if (!visited.contains(neighbor)) {
                dfs(pnbMap, neighbor, visited, subGraph);
            }
        }
    }

    public static int[] removeElement(int[] arr, int element) {
        if (arr == null) {
            return null;
        }
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == element) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return arr;
        }
        int[] newArray = new int[arr.length - 1];
        for (int i = 0, j = 0; i < arr.length; i++) {
            if (i == index) {
                continue;
            }
            newArray[j++] = arr[i];
        }
        return newArray;
    }

    public void removeElementsFromHashMap(int element, HashMap<Integer, int[]> map){
        // 遍历HashMap的键值对
        Iterator<Map.Entry<Integer, int[]>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, int[]> entry = iterator.next();
            int key = entry.getKey();
            int[] values = entry.getValue();

            if (key == element) {
                iterator.remove();
                continue;
            }

            StringBuilder sb = new StringBuilder();
            for (int value : values) {
                if (value != element) {
                    sb.append(value).append(" ");
                }
            }

            String[] updatedValues = sb.toString().trim().split(" ");
            int[] newArray = new int[updatedValues.length];
            for (int i = 0; i < updatedValues.length; i++) {
                newArray[i] = Integer.parseInt(updatedValues[i]);
            }
            entry.setValue(newArray);
        }
    }

    public static void removeElementsFromHashMap1(Set<Integer> valueToRemoveSet, Map<Integer, int[]>hashMap){
        for (int element: valueToRemoveSet){
            removeElementFromHashMap1(element, hashMap);
        }
    }

    public static void removeElementFromHashMap1(int valueToRemove, Map<Integer, int[]> hashMap) {
        Iterator<Map.Entry<Integer, int[]>> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, int[]> entry = iterator.next();
            if (entry.getKey() == valueToRemove) {
                iterator.remove();
            } else {
                int[] values = entry.getValue();
                List<Integer> updatedValues = new ArrayList<>();
                for (int value : values) {
                    if (value != valueToRemove) {
                        updatedValues.add(value);
                    }
                }
                if (updatedValues.size() < values.length) {
                    entry.setValue(updatedValues.stream().mapToInt(Integer::intValue).toArray());
                }
            }
        }
    }



    public IndexTree temp(String fileName, String prefix, int forTest, Map<Integer, int[]> pnbMap) throws Exception {
        long startTime = System.nanoTime();
        IndexTreePrinter indexTreePrinter = new IndexTreePrinter(fileName);
        IndexTree indexTree = new IndexTree();
        int[] vertex2 = {1, 0, 1};  // APA
        int[] edge2 = {3, 0};  // A->P, P->A
        MetaPath metaPath = new MetaPath(vertex2, edge2);
        IndexMPNode indexMPNode = new IndexMPNode(metaPath);
        indexTree.addChild(indexMPNode);


        if (forTest == 0) forTest = 5;
        IndexKNode indexKNodeLast = null;IndexKNode indexKNode = null;
        for (int i = forTest - 5 >= 0 ? forTest - 5 : 1; i <= forTest; i++) {
            indexKNodeLast = indexKNode;
            indexKNode = new IndexKNode(i, indexMPNode);
            indexMPNode.addKNodeChild(indexKNode);
            List<Map<Integer, int[]>> kCores = KConnectedComponents.getKConnectedComponents(pnbMap, i);
            if (kCores.size() == 0){
                throw new MyException("getKConnectedComponents失败");
            }
            for (Map<Integer, int[]> kCore : kCores) {
                for (int[] component : kCore.values()) {
//                    indexKNodeLast = indexKNode;
                    createIACHIndexTree(component, indexKNode, null, i, pnbMap, startTime);
                }
//                indexKNode.getChildren().clear();
            }
//            if (indexKNodeLast==null)
//            {
//                continue;
//            }  //FIXME
            indexKNode.setDirectVertices();
            indexKNode.calVertices();


            printIndexKTree(prefix, indexKNode, false); //FIXME 2

            // TODO 每个都固定存个k=1的存储情况
//            processAdvancedKNode(indexKNodeLast, indexKNode);
//            indexKNodeLast.calObVertices();
//            indexMPNode.setKVerticesMap(indexKNodeLast.getK(), indexKNodeLast.getObVertices());
////            indexTreePrinter.printIndexKTree(prefix + "     ", indexKNodeLast, true);
//            printIndexKTree(prefix, indexKNodeLast, true);  //FIXME

//            indexKNodeLast.getChildren().clear();
        }
        assert indexKNode != null;
        indexKNode.calObVertices();
        indexMPNode.setKVerticesMap(indexKNode.getK(), indexKNode.getObVertices());

//        indexTreePrinter.printIndexKTree(prefix + "     ", indexKNode, true);
//
//        printIndexKTree(prefix, indexKNode, true);  //FIXME

        return indexTree;
    }
}
