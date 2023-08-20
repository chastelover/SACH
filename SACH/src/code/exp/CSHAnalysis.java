package code.exp;

import code.util.Config;
import code.util.DataReader;
import code.exp.csh.FastBCore;
import code.online.MetaPath;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CSHAnalysis {
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


            int vertex[], edge[];
            int queryId = 0;
            if (reader == dblpReader) {
                vertex = new int[]{1, 0, 1}; // APA
                edge = new int[]{3, 0};
                queryId = 596172;
            } else if (reader == imdbReader) {
                vertex = new int[]{0, 1, 0}; // MAM
                edge = new int[]{0, 1};
                queryId = 337027;
            } else if (reader == fsqReader) {
                vertex = new int[]{0, 2, 0}; // RVR
                edge = new int[]{2, 3};
                queryId = 151;
            } else {
                vertex = new int[]{0, 1, 0}; // GDG
                edge = new int[]{0, 3};
                queryId = 9999;
            }
            MetaPath queryMPath = new MetaPath(vertex, edge);

            try{
                FileWriter write = new FileWriter("/" + reader.toString() + "CSH_Query.txt");
                BufferedWriter bw = new BufferedWriter(write);

                for(int queryK = 1;queryK <= 20;queryK++){
                    long t1 = System.nanoTime();
                    FastBCore quickCore = new FastBCore(graph, vertexType, edgeType);
                    Set<Integer> rsSet1 = quickCore.query(queryId, queryMPath, queryK);
                    long t2 = System.nanoTime();
                    if(rsSet1 != null)  System.out.println("|rsSet1|=" + rsSet1.size() + " time:" + (t2 - t1));
                    else System.out.println("rsSet1 is empty");

                    bw.write("queryK: " + queryK +"  TIME: " + (t2 - t1) + "\n");
                    if(rsSet1 != null){
                        for (int id : rsSet1) {
                            bw.write(id + " ");
                        }
                    }
                    else {
                        bw.write("empty!!");
                    }
                    bw.write("\n");
                }

                bw.close();
                write.close();
                System.out.println(reader.toString() + " done!");
            } catch(Exception e){
                e.printStackTrace();
            }

            System.out.println("ALL Done!");
        }
    }
}
