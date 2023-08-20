package code.online.basic;

import code.online.MetaPath;

import java.util.*;
import java.util.stream.Collectors;

public class BASIC {
    private int graph[][] = null;//data graph, including vertex IDs, edge IDs, and their link relationships
    private int vertexType[] = null;//vertex -> type
    private int edgeType[] = null;//edge -> type
    private double weight[] = null;//vertex -> weight

    private int queryId = -1;//the query vertex id
    private MetaPath queryMPath = null;//the query meta-path
    private int queryK = -1;//the threshold k
    private double fh;

    public BASIC(int graph[][], int vertexType[], int edgeType[], double weight[]) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.weight = weight;
    }

    public Set<Integer> query(int queryId, MetaPath queryMPath, int queryK) {
        this.queryId = queryId;
        this.queryMPath = queryMPath;
        this.queryK = queryK;

        //step 1: 检查 queryId 的类型是否与 meta-path 匹配
        if(queryMPath.vertex[0] != vertexType[queryId])   return null;

        //step 2: 构造同构子图
        Map<Integer, Set<Integer>> pnbMap = buildGraphNaive();

        //step 3: 计算连通的 k-core 并删除属性值较小的顶点
        Set<Integer> community = findMaximalInfluenceCommunity(pnbMap);

        return community;
    }

    private Map<Integer, Set<Integer>> buildGraphNaive() {
        //step 1: find all the vertices
        Set<Integer> keepSet = new HashSet<Integer>();
        for(int curId = 0;curId < graph.length;curId ++) {
            if(vertexType[curId] == queryMPath.vertex[0]) {
                keepSet.add(curId);
            }
        }

        //step 2: build the graph
        Map<Integer, Set<Integer>> pnbMap = new HashMap<Integer, Set<Integer>>();
        BatchSearch batchSearch = new BatchSearch(graph, vertexType, edgeType, queryMPath);
        for(int curId = 0;curId < graph.length;curId ++) {
            if(vertexType[curId] == queryMPath.vertex[0]) {
                Set<Integer> pnbSet = batchSearch.collect(curId, keepSet);
                pnbMap.put(curId, pnbSet);
            }
        }

        return pnbMap;
    }

    private Set<Integer> findMaximalInfluenceCommunity(Map<Integer, Set<Integer>> pnbMap) {
        Queue<Integer> queue = new LinkedList<Integer>();//simulate a queue

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
        Set<Integer> community = new HashSet<Integer>();
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
                    for(int pnb:pnbMap.get(curId)) {
                        if(!MaximalInfluenceCommunity.contains(pnb)) {
                            tmpQueue.add(pnb);
                            MaximalInfluenceCommunity.add(pnb);
                        }
                    }
                }
                fh = weight[ascendingVertex.get(0)];
                return set_merge(MaximalInfluenceCommunity, temp);
            }
        }
        pnbMap = null;
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

        return false;
    }

    public double getFh() {
        return fh;
    }
}
