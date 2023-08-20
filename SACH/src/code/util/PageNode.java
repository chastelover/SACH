package code.util;

import java.util.ArrayList;
import java.util.List;

public class PageNode implements Comparable<PageNode>{
    private Integer nodeName;//节点名字
    private List<Integer> edges=new ArrayList<>();//所有的边
    private double score=1;//页面权重,初始分数为1
    public PageNode(int nodeName){
        this.nodeName=nodeName;
    }
    public Integer getLen(){
        return edges.size();
    }
    public void setEdges(List<Integer> edges){
        this.edges=edges;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Integer getNodeName() {
        return nodeName;
    }

    public List<Integer> getEdges() {
        return edges;
    }

    @Override
    public int compareTo(PageNode node) {
        if(this.edges!=null&&node.edges!=null){
            if(node.getScore()>this.score){
                return 1;
            }else if(node.getScore()<this.score){
                return -1;
            }else{
                return 0;
            }
        }
        return 0;
    }
}