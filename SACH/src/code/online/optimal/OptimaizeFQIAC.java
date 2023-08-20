package code.online.optimal;

import code.online.MetaPath;
import code.online.basic.FQIAC;

import java.util.*;

public class OptimaizeFQIAC {
    private int graph[][] = null;//data graph, including vertex IDs, edge IDs, and their link relationships
    private int vertexType[] = null;//vertex -> type
    private int edgeType[] = null;//edge -> type
    private double weight[] = null;//vertex -> weight
    private int queryId = -1;//the query vertex id
    private int queryK = -1;//the threshold k

    public OptimaizeFQIAC(int graph[][], int vertexType[], int edgeType[], double weight[]) {
        this.graph = graph;
        this.vertexType = vertexType;
        this.edgeType = edgeType;
        this.weight = weight;
    }

    public HashMap<MetaPath,Set<Integer>> query(int queryId, List<MetaPath> queryMPathList, int queryK) {
                                                                                                                                                                                                                                                                                               this.queryId = queryId;
        this.queryK = queryK;

        List<List<MetaPath>> queryTaskList = new ArrayList<>();

//
        if (isSubPath(queryMPathList.get(0), queryMPathList.get(1))) {
//            System.out.println("sub ok");
            // 短元路径会与长元路径被分配到同一查询任务中
            List<MetaPath> queryTask = new ArrayList();
            queryTask.add(queryMPathList.get(0));
            queryTask.add(queryMPathList.get(1));
            queryTaskList.add(queryTask);
        }

        HashMap<MetaPath,Set<Integer>> queryResult = new HashMap();
        // 处理查询任务
        for (List<MetaPath> mataPathList: queryTaskList) {
            if (mataPathList.size() == 1) {
                // 单元路径查询
                MetaPath queryMPath = mataPathList.get(0);
                FQIAC fq = new FQIAC(graph, vertexType, edgeType, weight);
                Set<Integer> community = fq.query(this.queryId, queryMPath, this.queryK);
                queryResult.put(queryMPath, community);
                return queryResult;
            } else {
                // 双元路径查询（一般只会有两个，太长的元路径暂不考虑）
                MetaPath metaPath = mataPathList.get(0).pathLen > mataPathList.get(1).pathLen ? mataPathList.get(0) : mataPathList.get(1);
                MetaPath subMetaPath = mataPathList.get(0).pathLen > mataPathList.get(1).pathLen ? mataPathList.get(1) : mataPathList.get(0);
                FQIAC fq = new FQIAC(graph, vertexType, edgeType, weight);
                return fq.queryWithCache(this.queryId, subMetaPath, metaPath, this.queryK);
            }
        }
        return null;
    }

    private boolean isSubPath(MetaPath metaPath1, MetaPath metaPath2) {
//        MetaPath shortMP = metaPath1.vertex.length > metaPath2.vertex.length ? metaPath2 : metaPath1;
//        MetaPath longMP = metaPath1.vertex.length > metaPath2.vertex.length ? metaPath1 : metaPath2;
//
//        if (shortMP.vertex[0] != longMP.vertex[0]) return false;
//        else if (shortMP.vertex[shortMP.vertex.length - 1] != longMP.vertex[longMP.vertex.length - 1]) return false;
//        else{
//            int i = 0;
//            int j = 0;
//            while (i < shortMP.vertex.length && j < longMP.vertex.length){
//                if (shortMP.vertex[i] == longMP.vertex[j]){
//                    i++;
//                    j++;
//                }else{
//                    j++;
//                }
//            }
//            if (i == shortMP.vertex.length - 1) return true;
//            else return false;
//        }
        return true;
    }
}

