package code.index.basic;

import code.online.MetaPath;
import code.util.Config;
import code.util.DataReader;
import code.util.Dictionary;

import java.io.IOException;
import java.util.*;

import static code.index.KConnectedComponents.getKConnectedComponents;
import static code.index.TestIACHIndex.getConfig;
import static code.index.TestIACHIndex.getMetaPath;

public class BCore {
    public static void main(String[] args) throws IOException {
        String type = "dblp";
        Dictionary dictionary = new Dictionary();
        List<MetaPath> metaPathList = getMetaPath(type, dictionary);

        DataReader dataReader = getConfig(type);
        int[][] graph = dataReader.readGraph();
        int[] vertexType = dataReader.readVertexType();
        int[] edgeType = dataReader.readEdgeType();
        BCoreDecomposition b = new BCoreDecomposition(graph, vertexType, edgeType);

        assert metaPathList != null;
        for (MetaPath mp : metaPathList) {
            System.out.println("Meta-path: " + Arrays.toString(mp.vertexName));
            Map<Integer, int[]> pnbMap = b.decomposeAndGetGraph(mp);
            System.out.println(b.getMaxK());

//            List<Map<Integer, int[]>> kConnectedComponents = getKConnectedComponents(pnbMap, 64);
//            for (int i = 0; i < kConnectedComponents.size(); i++) {
////				for (int queryID:queryQList){
//                Map<Integer, int[]> connectedComponent = kConnectedComponents.get(i);
//                System.out.println("Connected Component " + (i + 1) + ":");
//                for (int key : connectedComponent.keySet()) {
//                    System.out.println("k = " + key + ", vertices = " + Arrays.toString(connectedComponent.get(key)));
//                }
////				}
////            }
//            }
        }
    }
}
