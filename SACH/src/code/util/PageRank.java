package code.util;

import java.util.*;

public class PageRank {
    private final static double DISTANCE=0.000001;//误差值
    private final static double d=0.85;//阻尼系数
    /*
    name_to_node:名字到节点转换
     */
    private static Map<Integer,PageNode> name_to_node=new HashMap<>();
    public static List<PageNode> build(){
        List<PageNode> graph=new ArrayList<>();

//        DataReader dataReader = new DataReader(Config.smallDBLPGraph, Config.smallDBLPVertex, Config.smallDBLPEdge, Config.smallDBLPVertexWeight);
//        DataReader dataReader = new DataReader(Config.dblpGraph, Config.dblpVertex, Config.dblpEdge, Config.dblpVertexWeight);
        DataReader dataReader = new DataReader(Config.IMDBGraph, Config.IMDBVertex, Config.IMDBVertex, Config.IMDBVertexWeight);
        int source_graph[][] = dataReader.readGraph();
        for (int i = 0; i < source_graph.length; i++) {
            List<Integer> edge=new ArrayList<>();
            PageNode pageNode = new PageNode(i);
            for (int j = 0; j < source_graph[i].length; j = j + 2) {
                edge.add(source_graph[i][j]);
            }
            pageNode.setEdges(new ArrayList<>(edge));
            name_to_node.put(pageNode.getNodeName(),pageNode);
            graph.add(pageNode);
        }

        return graph;
    }
    public static void Cal(List<PageNode> graph){
        double common=(1-d)/graph.size();
        while (true){
            for(PageNode node:graph){
                double sum=0;
                for(Integer name:node.getEdges()){
                    PageNode edgeNode=name_to_node.get(name);
                    sum+=(edgeNode.getScore()/edgeNode.getLen());
                }
                double newScore=common+d*sum;
                if(Math.abs(node.getScore()-newScore)<DISTANCE){
                    return;
                }else {
                    node.setScore(newScore);
                }
            }
        }
    }
}