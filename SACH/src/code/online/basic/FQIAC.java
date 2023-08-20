package code.online.basic;
import code.online.MetaPath;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.*;


public class FQIAC {
    private int graph[][] = null;//data graph, including vertex IDs, edge IDs, and their link relationships
    private int vertexType[] = null;//vertex -> type
    private int edgeType[] = null;//edge -> type
    private double weight[] = null;//vertex -> weight
    int query_i = 0;

    private int queryId = -1;//the query vertex id
    private MetaPath queryMPath = null;//the query meta-path
    private int queryK = -1;//the threshold k
    private long timeset = 0;

    public FQIAC(int graph[][], int vertexType[], int edgeType[], double weight[]) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.weight = weight;
    }

    public Set<Integer> query(int queryId, MetaPath queryMPath, int queryK) {
        query_i += 1;
        DecimalFormat df = new DecimalFormat("#.00");
        long temp_t1 = System.nanoTime();
        this.queryId = queryId;
        this.queryMPath = queryMPath;
        this.queryK = queryK;

        //step 1: 检查 queryId 的类型是否与 meta-path 匹配
        if(queryMPath.vertex[0] != vertexType[queryId])   return null;

        //step 2: 构造同构子图
        Map<Integer, Set<Integer>> pnbMap = buildGraph();



        //step 3: 计算连通的 k-core 并删除属性值较小的顶点
        Set<Integer> community = findMaximalInfluenceCommunity(pnbMap);
//        Set<Integer> community = findKCore(pnbMap);
        // FIXME temp:为了实验图画属性社区和非属性社区
        System.out.println("----------属性社区size----------" + community.size());
        // FIXME temp:为了实验图画属性社区和非属性社区
        long temp_t2 = System.nanoTime();
//        System.out.print("temp_t"+query_i);
//        System.out.println(": "+ df.format((temp_t2 - temp_t1)/1000000000.00));
        return community;
    }

    public HashMap<MetaPath,Set<Integer>> queryWithCache(int queryId, MetaPath subMetaPath, MetaPath metaPath, int queryK) {
        long t1 = System.nanoTime();
        this.queryId = queryId;
        this.queryMPath = metaPath;
        this.queryK = queryK;

        //step 1: 检查 queryId 的类型是否与 meta-path 匹配
        if(queryMPath.vertex[0] != vertexType[queryId])   return null;
        Set<Integer> keepSet = null;


        //step 2: 构造同构子图
        // step 2.1: 找到所有与 q 有 P-connected 关系的顶点
        DecimalFormat df = new DecimalFormat("#.00");
        BatchLinker batchLinker = new BatchLinker(graph, vertexType, edgeType);
        keepSet = batchLinker.link(queryId, queryMPath);

        // step 2.2: 构造同构子图
        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();
        BatchSearch batchSearch = new BatchSearch(graph, vertexType, edgeType, queryMPath);
        for(int curId:keepSet) {
            Set<Integer> pnbSet = batchSearch.collect(curId, keepSet);
            pnbMap.put(curId, pnbSet);
        }
        // step 3: 计算连通的 k-core 并删除属性值较小的顶点
        Set<Integer> community = findMaximalInfluenceCommunity(pnbMap);
        long t2 = System.nanoTime();
//        System.out.println("rsSet1 time(s): " + df.format((t2 - t1)/1000000000.00));

        // step 2.3: 为子元路径快速构造同构子图
        Map<Integer, Set<Integer>> subPnbMap;
//        subPnbMap = subBatchSearch.collectAndBuildGraph2(keepSet, keepSet);  // 处理长度6和4
//        subPnbMap = subBatchSearch.collectAndBuildGraph(keepSet, keepSet); // 处理长度4和2
        long t7 = System.nanoTime();
//        subPnbMap = buildSubGraph0425(queryId, keepSet, subMetaPath);
        subPnbMap = buildSubGraph0511_1(queryId, keepSet, subMetaPath);
        long t8 = System.nanoTime();
//        System.out.println("sub time(s): " + df.format((t8 - t7)/1000000000.00));
//        System.out.println("xxx time(s): " + df.format(timeset/1000000000.00));

        // step 3: 计算连通的 k-core 并删除属性值较小的顶点
        assert subPnbMap != null;
        Set<Integer> subCommunity = findMaximalInfluenceCommunity(subPnbMap);
        long t3 = System.nanoTime();
//        System.out.println("rsSet2 time(s): " + df.format((t3 - t7)/1000000000.00));

        HashMap<MetaPath,Set<Integer>> result = new HashMap<MetaPath,Set<Integer>>();
        result.put(metaPath, community);
        result.put(subMetaPath, subCommunity);

        return result;
    }

    private Map<Integer, Set<Integer>> buildSubGraph0425(int queryId, Set<Integer> keepSet, MetaPath subMetaPath) {
        if (!keepSet.contains(queryId)) return null;

        Map<Integer, Set<Integer>> subPnbMap = new HashMap<>();
        BatchSearch batchSearch1 = new BatchSearch(graph, vertexType, edgeType, subMetaPath);;
        Queue<Integer> queue = new LinkedList<Integer>();
        Set<Integer> visited = new HashSet<>();
        queue.add(queryId);
        while (queue.size() > 0){
            int v = queue.poll();
            // 得到 v 的 P-邻居
            long m1 = System.nanoTime();
            Set<Integer> pnbSet = batchSearch1.collect0509(v, keepSet);
            long m2 = System.nanoTime();
//            System.out.println("m2-m1(ns):" + (m2-m1) );
            timeset = timeset + (m2-m1);
            // 与长元路径得到的目标类型顶点集 H 取交
//            Set<Integer> intersectionSet = new HashSet<>();
//            intersectionSet.addAll(pnbSet);
//            intersectionSet.retainAll(keepSet);
            if (pnbSet.size() >= queryK) {
                subPnbMap.put(v, pnbSet);  // v -> v
            }
            visited.add(v);
            for (int pnb: pnbSet) {
                if (!visited.contains(pnb)){
                    queue.add(pnb);
                }
            }
        }
        return subPnbMap;
    }

    private Map<Integer, Set<Integer>> buildSubGraph0509(int queryId, Set<Integer> keepSet, MetaPath subMetaPath){
        this.queryId = queryId;
        this.queryMPath = subMetaPath;
        if (!keepSet.contains(queryId)) return null;
        if(queryMPath.vertex[0] != vertexType[queryId])   return null;

//        step 2: 构造同构子图
//        step 2.1.0: 找到全部的q邻居再取交集做比
        BatchLinker batchLinker = new BatchLinker(graph, vertexType, edgeType);
        Set<Integer> q_p_connected_Set = batchLinker.link(this.queryId, this.queryMPath);  // 找一阶P邻居


        // step 2.2: 构造同构子图
        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();


        for(int curId:q_p_connected_Set) {
            BatchSearch batchSearch = new BatchSearch(graph, vertexType, edgeType, queryMPath);
            Set<Integer> pnbSet = batchSearch.collect(curId, keepSet);
            pnbMap.put(curId, pnbSet);
        }

        return pnbMap;

    }

//    private Map<Integer, Set<Integer>> buildSubGraph0511(int queryId, Set<Integer> keepSet, MetaPath subMetaPath){
//        this.queryId = queryId;
//        this.queryMPath = subMetaPath;
//        if (!keepSet.contains(queryId)) return null;
//        if(queryMPath.vertex[0] != vertexType[queryId])   return null;
//        Set visited = new HashSet<>(queryId);
//
//        // step 2.1: 找q的一阶p邻居
//        BatchSearch batchSearch = new BatchSearch(graph, vertexType, edgeType, queryMPath);
//        Queue<Integer> q_p_connected_queue = batchSearch.collect0511(queryId, keepSet);
//
//        // step 2.2: 构造同构子图
//        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();
//
//        while(q_p_connected_queue.size() > 0 ){
//            int curId = q_p_connected_queue.poll();
//            List<Set> resultList = batchSearch.collectWithCache(curId, keepSet, visited);
//            Set<Integer> pnbSet = resultList.get(0);
//            visited = resultList.get(1);
//            pnbMap.put(curId, pnbSet);
//            q_p_connected_queue.addAll(pnbSet);
//        }
//        return pnbMap;
//
//    }

    private Map<Integer, Set<Integer>> buildSubGraph0511_1(int queryId, Set<Integer> keepSet, MetaPath subMetaPath){
        this.queryId = queryId;
        this.queryMPath = subMetaPath;
        if (!keepSet.contains(queryId)) return null;
        if(queryMPath.vertex[0] != vertexType[queryId])   return null;
        Set<Integer> visited = new HashSet<>();
        visited.add(queryId);

        // step 2.1: 找q的一阶p邻居
        BatchSearch batchSearch = new BatchSearch(graph, vertexType, edgeType, queryMPath);
        Set<Integer>query_p_connected_set = batchSearch.collect0509(queryId, keepSet);

        // step 2.2: 构造同构子图
        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();
        pnbMap.put(queryId, query_p_connected_set);
        liu(query_p_connected_set, visited, keepSet, batchSearch, pnbMap);
//        System.out.println(pnbMap.size());
        return pnbMap;

    }

    private Map<Integer, Set<Integer>> liu (Set<Integer> query_p_connected_set, Set<Integer> visited,  Set<Integer> keepSet, BatchSearch batchSearch
    ,Map<Integer, Set<Integer>>pnbMap){
        Set<Integer> allPnbSet = new HashSet<>();
        int size = 0;
        for (int curId:query_p_connected_set){
            size += 1;
            if (visited.contains(curId)) {
                if (size != query_p_connected_set.size()) {
                    continue;  //FIXME bug由于恰好最后一个顶点是访问过的，所以continue了没有继续递归，已解决
                }
                liu(allPnbSet, visited, keepSet, batchSearch, pnbMap);
            }

            List<Set>resultList= batchSearch.collectWithCache(curId, keepSet, visited);
            Set<Integer> pnbSet = resultList.get(0);
            Set<Integer> realPnbSet = resultList.get(1);
            visited.add(curId);
            allPnbSet.addAll(pnbSet);
            pnbMap.put(curId, realPnbSet);
            if (size == query_p_connected_set.size()){
                liu(allPnbSet, visited, keepSet, batchSearch, pnbMap);
            }
        }
        return pnbMap;
    }

    private Map<Integer, Set<Integer>> buildGraph() {
        // 该过程与论文中 GCMP 算法可视为等同
        //step 1: 找到所有与 q 有 P-connected 关系的顶点
        BatchLinker batchLinker = new BatchLinker(graph, vertexType, edgeType);
        Set<Integer> keepSet = batchLinker.link(queryId, queryMPath);


        //step 2: 构造图
        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();
        BatchSearch batchSearch = new BatchSearch(graph, vertexType, edgeType, queryMPath);
        for(int curId:keepSet) {
            Set<Integer> pnbSet = batchSearch.collect(curId, keepSet);
            pnbMap.put(curId, pnbSet);
        }

        return pnbMap;
    }

    private Set<Integer> findMaximalInfluenceCommunity(Map<Integer, Set<Integer>> pnbMap) {
//        System.out.println("findMaximalInfluenceCommunity:pnbMap.size():" + pnbMap.size());
        if (pnbMap == null){
            return null;
        }
        Queue<Integer> queue = new LinkedList<Integer>();
        for(Map.Entry<Integer, Set<Integer>> entry: pnbMap.entrySet()){
            if (entry.getValue().size() < 1){
//                System.out.println(entry.getKey());
            }
        }
        // step 1: 计算最大连通 k-core
        //step 1.1: 找到度数小于 k 的顶点并加添加至删除队列
        Set<Integer> deleteSet = new HashSet<Integer>();
        for(Map.Entry<Integer, Set<Integer>> entry : pnbMap.entrySet()) {
            int curId = entry.getKey();
            Set<Integer> pnbSet = entry.getValue();
            if(pnbSet.size() < queryK) {
                queue.add(curId);
                deleteSet.add(curId);
            }
        }

        //step 1.2: 依次删除队列中的顶点并维护删除队列
        while(queue.size() > 0) {
            int curId = queue.poll();//delete curId
            Set<Integer> pnbSet = pnbMap.get(curId);
            for(int pnb:pnbSet) {//update curId's pnb
                if(!deleteSet.contains(pnb)) {
                    Set<Integer> tmpSet = pnbMap.get(pnb);
                    tmpSet.remove(curId);
                    if(tmpSet.size() < queryK) {
                        queue.add(pnb);
                        deleteSet.add(pnb);
                    }
                }
            }
            pnbMap.remove(curId); //删除当前顶点
        }

        //step 1.3: 计算包含 q 的最大连通子图
        if(pnbMap.get(queryId) == null)   return null;
        Set<Integer> community = new HashSet<Integer>(100000);
        Queue<Integer> ccQueue = new LinkedList<Integer>();
        ccQueue.add(queryId);
        community.add(queryId);
        while(ccQueue.size() > 0) {
            int curId = ccQueue.poll();
            for(int pnb:pnbMap.get(curId)) {//enumerate curId's neighbors
                if(!community.contains(pnb)) {
                    ccQueue.add(pnb);
                    community.add(pnb);
                }
            }
        }

        // FIXME temp:为了实验图画属性社区和非属性社区
        System.out.println();
        System.out.println("==========不计属性的社区size===========" + community.size());
        System.out.println();
        // FIXME temp:为了实验图画属性社区和非属性社区

        // step 2: 逐步删除图中属性值最小的顶点
        List<Integer> ascendingVertex = new LinkedList<>(community);
        //按照权重升序排列
        ascendingVertex.sort((o1, o2) -> Double.compare(weight[o1], weight[o2]));

//        System.out.println("普通图中包含的顶点数量: " + ascendingVertex.size());
        while(ascendingVertex.size() > 0) {
            Set<Integer> temp = new HashSet<Integer>();
            int minId = ascendingVertex.get(0);
            if (deleteMinWeightVertex(minId, queryId, ascendingVertex, temp, pnbMap)) {
                Set<Integer> MaximalInfluenceCommunity = new HashSet<Integer>();
                Queue<Integer> tmpQueue = new LinkedList<Integer>();
                tmpQueue.add(queryId);
                MaximalInfluenceCommunity.add(queryId);
                while(tmpQueue.size() > 0) {
                    int curId = tmpQueue.poll();
                    if (pnbMap.get(curId)!=null) {
                        for (int pnb : pnbMap.get(curId)) {
                            if (!MaximalInfluenceCommunity.contains(pnb)) {
                                tmpQueue.add(pnb);
                                MaximalInfluenceCommunity.add(pnb);
                            }
                        }
                    }
                }
                return set_merge(MaximalInfluenceCommunity, temp);
            }
        }

        return null;
    }

    public static <T> Set<T> set_merge(Set <T> set_1, Set<T> set_2){
        Set<T> my_set = set_1.stream().collect(Collectors.toSet());
        my_set.addAll(set_2);
        return my_set;
    }

    private boolean deleteMinWeightVertex(int minId, int queryId, List<Integer> ascendingVertex, Set<Integer> temp, Map<Integer, Set<Integer>> pnbMap) {

        //初始化一个删除队列
        Queue<Integer> deleteQueue = new LinkedList<Integer>();
        deleteQueue.add(minId);
        Set<Integer> deleteSet = new HashSet<Integer>();
        deleteSet.add(minId);

        //迭代删除权重最小的顶点
        while(deleteQueue.size() > 0) {
            int curId = deleteQueue.poll();
            //当删除顶点为 q 时，停止删除
            if(curId == queryId)    return true;
            Set<Integer> pnbSet = pnbMap.get(curId);
            for(int pnb:pnbSet) {
                if(!deleteSet.contains(pnb)) {
                    Set<Integer> tmpSet = pnbMap.get(pnb);
                    tmpSet.remove(curId);
                    if(tmpSet.size() < queryK) {
                        deleteQueue.add(pnb);
                        deleteSet.add(pnb);
                    }
                }
            }
            pnbMap.remove(curId); //删除当前顶点
            ascendingVertex.remove((Integer)curId);
            temp.add(curId);
        }

//        Set<Integer> connectedKcore = new HashSet<Integer>();
//        Queue<Integer> ccQueue = new LinkedList<Integer>();
//        ccQueue.add(queryId);
//        connectedKcore.add(queryId);
//        while(ccQueue.size() > 0) {
//            int curId = ccQueue.poll();
//            for(int pnb:pnbMap.get(curId)) {
//                if(!connectedKcore.contains(pnb)) {
//                    ccQueue.add(pnb);
//                    connectedKcore.add(pnb);
//                }
//            }
//        }
//
//
//        Set<Integer> unconnected = new HashSet<Integer>();
//        unconnected.addAll(ascendingVertex);
//        unconnected.removeAll(connectedKcore);
//
//
//        for (int v : unconnected) {
//            ascendingVertex.remove((Integer) v);
//        }

        return false;
    }

    private Set<Integer> findKCore(Map<Integer, Set<Integer>> pnbMap) {
        Queue<Integer> queue = new LinkedList<Integer>();//simulate a queue

        //step 1: find the vertices can be deleted in the first round
        Set<Integer> deleteSet = new HashSet<Integer>();
        for(Map.Entry<Integer, Set<Integer>> entry : pnbMap.entrySet()) {
            int curId = entry.getKey();
            Set<Integer> pnbSet = entry.getValue();
            if(pnbSet.size() < queryK) {
                queue.add(curId);
                deleteSet.add(curId);
            }
        }

        //step 2: delete vertices whose degrees are less than k
        while(queue.size() > 0) {
            int curId = queue.poll();//delete curId
            Set<Integer> pnbSet = pnbMap.get(curId);
            for(int pnb:pnbSet) {//update curId's pnb
                if(!deleteSet.contains(pnb)) {
                    Set<Integer> tmpSet = pnbMap.get(pnb);
                    tmpSet.remove(curId);
                    if(tmpSet.size() < queryK) {
                        queue.add(pnb);
                        deleteSet.add(pnb);
                    }
                }
            }
            pnbMap.put(curId, new HashSet<Integer>());//clean all the pnbs of curId
        }

        //step 3: find the connected component containing q
        if(pnbMap.get(queryId).size() < queryK)   return null;
        Set<Integer> community = new HashSet<Integer>();//vertices which have been put into queue
        Queue<Integer> ccQueue = new LinkedList<Integer>();
        ccQueue.add(queryId);
        community.add(queryId);
        while(ccQueue.size() > 0) {
            int curId = ccQueue.poll();
            for(int pnb:pnbMap.get(curId)) {//enumerate curId's neighbors
                if(!community.contains(pnb)) {
                    ccQueue.add(pnb);
                    community.add(pnb);
                }
            }
        }
        return community;
    }
}
