package code.exp;

import code.util.Config;
import code.util.DataReader;
import code.exp.csh.FastBCore;
import code.online.MetaPath;
import code.online.basic.BASIC;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IACHINAnalysis {
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
            if (reader == dblpReader) {
                vertex = new int[]{1, 0, 1}; // APA
                edge = new int[]{3, 0};
                queryId = 596172;
                vertex_number = 785104;
            } else if (reader == imdbReader) {
                vertex = new int[]{0, 1, 0}; // MAM
                edge = new int[]{0, 1};
                queryId = 312729 ;
                vertex_number = 176674 ;
            } else if (reader == fsqReader) {
                vertex = new int[]{0, 2, 0}; // RVR
                edge = new int[]{2, 3};
                queryId = 4207999   ;
                vertex_number = 984852 ;
            } else {
                vertex = new int[]{0, 1, 0}; // GDG
                edge = new int[]{0, 3};
                queryId = 9999;
                vertex_number = 1186;
            }
            MetaPath queryMPath = new MetaPath(vertex, edge);

            try{
                FileWriter write = new FileWriter("/" + reader.toString() + "IACHIN_Query.txt");
                BufferedWriter bw = new BufferedWriter(write);
                FileWriter write2 = new FileWriter("/" + reader.toString() + "CSH_Query.txt");
                BufferedWriter bw2 = new BufferedWriter(write2);

                for(int queryK = 1;queryK <= 20;queryK++){
                    long t1 = System.nanoTime();
                    BASIC prunePath = new BASIC(graph, vertexType, edgeType, weight);
                    Set<Integer> rsSet1 = prunePath.query(queryId, queryMPath, queryK);
                    long t2 = System.nanoTime();

                    FastBCore quickCore = new FastBCore(graph, vertexType, edgeType);
                    Set<Integer> rsSet2 = quickCore.query(queryId, queryMPath, queryK);
                    long t3 = System.nanoTime();

                    System.out.println("queryK: " + queryK);
                    DecimalFormat df = new DecimalFormat("#.00");
                    if(rsSet1 != null)  System.out.println("|rsSet1|=" + rsSet1.size() + " Percentage: " +(float)rsSet1.size()/(float)vertex_number + " time:" +  df.format((t2 - t1)/1000000000.00));
                    else System.out.println("rsSet1 is empty");
                    if(rsSet2 != null)  System.out.println("|rsSet2|=" + rsSet2.size() + " Percentage: " +(float)rsSet2.size()/(float)vertex_number + " time:" +  df.format((t3 - t2)/1000000000.00));
                    else System.out.println("rsSet2 is empty");


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

                    bw2.write("queryK: " + queryK +"  TIME: " + (t2 - t1) + "\n");
                    if(rsSet2 != null){
                        for (int id : rsSet2) {
                            bw2.write(id + " ");
                        }
                    }
                    else {
                        bw2.write("empty!!");
                    }
                    bw2.write("\n");
                }

                bw.close();
                write.close();
                bw2.close();
                write2.close();
                System.out.println(reader.toString() + " done!");
            } catch(Exception e){
                e.printStackTrace();
            }

            System.out.println("ALL Done!");
        }
    }
}
