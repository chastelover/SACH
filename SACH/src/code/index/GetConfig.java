package code.index;

import code.index.basic.*;
import code.index.tree.IndexTree;
import code.index.tree.IndexTreePrinter;
import code.util.Config;
import code.util.DataReader;
import code.online.MetaPath;
import code.util.Dictionary;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static code.index.tree.IndexKTree.*;
import static code.util.CurrentTimeFiller.fillFilename;

public class GetConfig {

    /**
     * @Description : 对不同的数据集，返回不同的三种元路径
     * @param name
     * @return
     */

    /**
     * @Description : 根据数据集名称获得配置
     * @param name ： 数据集名
     * @return
     */
    public static DataReader getConfig(String name){
        name = name.toLowerCase();
        DataReader dataReader = null;
        switch (name) {
            case "dblp" -> {
                dataReader = new DataReader(Config.dblpGraph, Config.dblpVertex, Config.dblpEdge, Config.dblpVertexWeight);
                break;
            }
            case "foursquare" -> {
                dataReader = new DataReader(Config.FsqGraph, Config.FsqVertex, Config.FsqEdge, Config.FsqVertexWeight);
                break;
            }
            case "imdb" -> {
                dataReader = new DataReader(Config.IMDBGraph, Config.IMDBVertex, Config.IMDBEdge, Config.IMDBVertexWeight);
                break;
            }
            case "pubmed" -> {
                dataReader = new DataReader(Config.PubMedGraph, Config.PubMedVertex, Config.PubMedEdge, Config.PubMedVertexWeight);
                break;
            }
            case "test1" -> {
                dataReader = new DataReader(Config.likeDBLPGraph, Config.likeDBLPVertex, Config.likeDBLPEdge, Config.likeDBLPVertexWeight);
            }
            case "test2" -> {
                dataReader = new DataReader(Config.likeDBLPGraph2, Config.likeDBLPVertex2, Config.likeDBLPEdge2, Config.likeDBLPVertexWeight2);
            }
        }
        return dataReader;
    }


    public static List<MetaPath> getMetaPath(String name, Dictionary dictionary) throws IOException {
        name = name.toLowerCase();
        Map<Integer, String> id2Vertex = dictionary.id2Vertex;
        Map<Integer, String>id2Edge = dictionary.id2Edge;
        List<MetaPath> metaPathList = new ArrayList<MetaPath>();
        switch (name) {
            case "dblp","test", "test1", "test2" -> {
                dictionary.loadMappingsFromFile(Config.dblpReadme);
                int[]vertex = {1, 0, 3, 0, 1}; // APTPA
                int[]edge = {3, 2, 5, 0}; //A->P, P->T, T->P, P->A
                MetaPath metaPath = new MetaPath(vertex, edge);
                metaPath.convertId2Name(id2Vertex, id2Edge);
                metaPathList.add(metaPath);

//                int[]vertex1 = {1, 0, 2, 0, 1};  // APVPA
//                int[]edge1 = {3, 1, 4, 0}; //A->P, P->V, V->P, P->A
//                MetaPath metaPath1 = new MetaPath(vertex1, edge1);
//                metaPath1.convertId2Name(id2Vertex, id2Edge);
//                metaPathList.add(metaPath1);
//
//                int[]vertex2 = {1, 0, 1};  // APA
//                int[]edge2 = {3, 0};  // A->P, P->A
//                MetaPath metaPath2 = new MetaPath(vertex2, edge2);
//                metaPath2.convertId2Name(id2Vertex, id2Edge);
//                metaPathList.add(metaPath2);
                return metaPathList;
            }
            case "foursquare" -> {
                dictionary.loadMappingsFromFile(Config.FsqReadme);
//                int[]vertex = {1, 0, 3, 0, 1}; // URCRU
//                int[]edge = {1, 4, 5, 0}; //U->R, R->C, C->R, R->U
//                MetaPath metaPath = new MetaPath(vertex, edge);
//                metaPath.convertId2Name(id2Vertex, id2Edge);
//                metaPathList.add(metaPath);

//                int[]vertex1 = {1, 0, 2, 0, 1};  // URVRU
//                int[]edge1 = {1, 2, 3, 0}; //U->R, R->V, V->R, R->U
//                MetaPath metaPath1 = new MetaPath(vertex1, edge1);
//                metaPath1.convertId2Name(id2Vertex, id2Edge);
//                metaPathList.add(metaPath1);

//                int[]vertex2 = {1, 0, 1};  // URU
//                int[]edge2 = {1, 0};  // U->R, R->U
//                MetaPath metaPath2 = new MetaPath(vertex2, edge2);
//                metaPath2.convertId2Name(id2Vertex, id2Edge);
//                metaPathList.add(metaPath2);

                int[]vertex3 = {0, 2, 0};  // RVR
                int[]edge3 = {2, 3};  // R->V, V->R
                MetaPath metaPath3 = new MetaPath(vertex3, edge3);
                metaPath3.convertId2Name(id2Vertex, id2Edge);
                metaPathList.add(metaPath3);

//                int[]vertex4 = {0, 3, 0};  // RCR
//                int[]edge4 = {4, 5};  // R->C, C->R
//                MetaPath metaPath4 = new MetaPath(vertex4, edge4);
//                metaPath4.convertId2Name(id2Vertex, id2Edge);
//                metaPathList.add(metaPath4);

                return metaPathList;
            }
            case "imdb" -> {
                dictionary.loadMappingsFromFile(Config.IMDBReadme);
//                int[]vertex = {1, 0, 3, 0, 1}; // AMWMA
//                int[]edge = {1, 4, 5, 0}; //A->M, M->W, W->M, M->A
//                MetaPath metaPath = new MetaPath(vertex, edge);
//                metaPath.convertId2Name(id2Vertex, id2Edge);
//                metaPathList.add(metaPath);
//////
//                int[]vertex1 = {1, 0, 2, 0, 1};  // AMDMA
//                int[]edge1 = {1, 2, 3, 0}; //A->M, M->D, D->M, M->A
//                MetaPath metaPath1 = new MetaPath(vertex1, edge1);
//                metaPath1.convertId2Name(id2Vertex, id2Edge);
//                metaPathList.add(metaPath1);

                int[]vertex2 = {1, 0, 1};  // AMA
                int[]edge2 = {1, 0};  // A->M, M->A
                MetaPath metaPath2 = new MetaPath(vertex2, edge2);
                metaPath2.convertId2Name(id2Vertex, id2Edge);
                metaPathList.add(metaPath2);
                return metaPathList;
            }
            case "pubmed" -> {
                dictionary.loadMappingsFromFile(Config.PubMedReadme);
                int[]vertex = {0, 1, 0}; // GDG
                int[]edge = {0, 3}; // G->D, D->G
                MetaPath metaPath = new MetaPath(vertex, edge);
                metaPath.convertId2Name(id2Vertex, id2Edge);
                metaPathList.add(metaPath);

                int[]vertex1 = {0, 2, 0};  // GCG
                int[]edge1 = {1, 4}; //A->M, M->D, D->M, M->A
                MetaPath metaPath1 = new MetaPath(vertex1, edge1);
                metaPath1.convertId2Name(id2Vertex, id2Edge);
                metaPathList.add(metaPath1);

                int[]vertex2 = {0, 3, 0};  // GSG
                int[]edge2 = {2, 5};  // A->M, M->A
                MetaPath metaPath2 = new MetaPath(vertex2, edge2);
                metaPath2.convertId2Name(id2Vertex, id2Edge);
                metaPathList.add(metaPath2);
                return metaPathList;
            }
        }
        System.out.println("输入type有误");
        return null;
    }

}
