package code.exp;

import code.util.Config;
import code.util.DataReader;
import code.online.MetaPath;
import code.online.basic.BASIC;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        DataReader dblpReader = new DataReader(Config.dblpGraph, Config.dblpVertex, Config.dblpEdge, Config.dblpVertexWeight);
        DataReader imdbReader = new DataReader(Config.IMDBGraph, Config.IMDBVertex, Config.IMDBEdge, Config.IMDBVertexWeight);
        DataReader fsqReader = new DataReader(Config.FsqGraph, Config.FsqVertex, Config.FsqEdge, Config.FsqVertexWeight);
        DataReader pmReader = new DataReader(Config.PubMedGraph, Config.PubMedVertex, Config.PubMedEdge, Config.PubMedVertexWeight);
        List<DataReader> readers = new ArrayList<>();
        readers.add(dblpReader);
        readers.add(imdbReader);
        readers.add(fsqReader);
        readers.add(pmReader);

        for (DataReader reader : readers) {
            int graph[][] = reader.readGraph();
            int vertexType[] = reader.readVertexType();
            int edgeType[] = reader.readEdgeType();
            double weight[] = reader.readVertexWeight();


            int vertex[], edge[];
            int queryId = 0;
            int vertex_number = 0;
            int max_core = 0;
            if (reader == dblpReader) {
                vertex = new int[]{1, 0, 1}; // APA
                edge = new int[]{3, 0};
                queryId = 596172;
                vertex_number = 785104;
                max_core =113;
            } else if (reader == imdbReader) {
                vertex = new int[]{0, 1, 0}; // MAM
                edge = new int[]{0, 1};
                queryId = 312729;
                vertex_number = 176674;
                max_core = 2922;
            } else if (reader == fsqReader) {
                vertex = new int[]{0, 2, 0}; // RVR
                edge = new int[]{2, 3};
                queryId = 4207999;
                vertex_number = 984852;
                max_core = 2786;
            } else {
                vertex = new int[]{0, 1, 0}; // GDG
                edge = new int[]{0, 3};
                queryId = 9999;
                vertex_number = 1186;
                max_core = 615;
            }
            MetaPath queryMPath = new MetaPath(vertex, edge);

            for(int queryK = 1;queryK <= max_core;queryK++){
                long t1 = System.nanoTime();
                BASIC prunePath = new BASIC(graph, vertexType, edgeType, weight);
                Set<Integer> rsSet1 = prunePath.query(queryId, queryMPath, queryK);
                long t2 = System.nanoTime();
                System.out.println("queryK: " + queryK);
                double FH = 999999;
                if(rsSet1 != null)  {
                    for (int id : rsSet1) {
                        if (weight[id]<FH){
                            FH = weight[id];
                        }
                    }
                    System.out.println("社区值： "+ FH);
                }
                else System.out.println("rsSet1 is empty");
            }
            System.out.println("ALL Done!");
        }
    }
}
