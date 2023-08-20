package code.exp;

import code.util.Config;
import code.util.DataReader;
import code.index.basic.BCoreDecomposition;
import code.online.MetaPath;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoreDecomposition {
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
            if (reader == dblpReader) {
                vertex = new int[]{1, 0, 1}; // APA
                edge = new int[]{3, 0};
            } else if (reader == imdbReader) {
                vertex = new int[]{0, 1, 0}; // MAM
                edge = new int[]{0, 1};
            } else if (reader == fsqReader) {
                vertex = new int[]{0, 2, 0}; // RVR
                edge = new int[]{2, 3};
            } else {
                vertex = new int[]{0, 1, 0}; // GDG
                edge = new int[]{0, 3};
            }
            MetaPath queryMPath = new MetaPath(vertex, edge);

            BCoreDecomposition bcd = new BCoreDecomposition(graph, vertexType, edgeType);
            Map<Integer, Integer> map = bcd.decompose(queryMPath);

            try{
                FileWriter write = new FileWriter("/" + reader.toString() + "coreDecomposition.txt");
                BufferedWriter bw = new BufferedWriter(write);

                int max = 0;
                for(Map.Entry<Integer, Integer> entry:map.entrySet()) {
                    bw.write(entry.getKey() + " " + entry.getValue() + "\n");
                    if(entry.getValue() >= max) {
                        max = entry.getValue();
                    }
                }

                bw.close();
                write.close();
                System.out.println(reader.toString() + "  max=" + max);
            } catch(Exception e){
                e.printStackTrace();
            }
            System.out.println("Done!");
        }
    }
}
