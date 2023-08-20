package code.index.tree;

import code.online.MetaPath;

import java.util.*;

public class IndexMPNode {
    public String[] vertexName;
    private MetaPath metaPath;
    private int metaPathLength;  // 仅考虑如节点间的跳数，如APA.len=2，APTPA.len=4,APVPA.len=4
    private List<IndexKNode> kNodesChildren = new ArrayList<>();
    private int kMax;
    private Map<Integer, List<Integer>> kVerticesMap= new HashMap<>();
    private Map<Integer, IndexMPNode> metaPathNodeChildren = new HashMap<>();
    // TODO k,v 分别为元路径长度、元路径节点，考虑用长元路径节点cover短的
    private Map<Integer, IndexMPNode> metaPathNodeParent = new HashMap<>();

    public IndexMPNode(MetaPath metaPath){
        this.metaPath = metaPath;
        this.vertexName = metaPath.vertexName;
        this.metaPathLength = metaPath.pathLen;
    }

    public void setKVerticesMap(int k, List<Integer>list){
        kVerticesMap.put(k, list);
    }

    public Map<Integer, List<Integer>> getKVerticesMap(){
        return kVerticesMap;
    }

    public void setMetaPath(MetaPath metaPath){
        this.vertexName = metaPath.vertexName;
        this.metaPath = metaPath;
        this.metaPathLength = metaPath.pathLen;
    }

    public MetaPath getMetaPath(){
        return metaPath;
    }

    public void setMetaPathLength(int metaPathLength){
        this.metaPathLength = metaPathLength;
    }

    public int getMetaPathLength(){
        return metaPathLength;
    }

    public void setKNodesChildren(List<IndexKNode> kNodesChildren){
        this.kNodesChildren = kNodesChildren;
    }

    public void setKNodesChildren(IndexKNode indexKNode){
        this.kNodesChildren.add(indexKNode);
        if (indexKNode.getK() > kMax){
            kMax = indexKNode.getK();
        }
    }

    public int calKMax(){
        return kNodesChildren.size();
    }

    public List<IndexKNode> getKNodesChildren(){
        return kNodesChildren;
    }

    public void setKMax(int kMax){
        this.kMax = kMax;
    }

    public Map<Integer, IndexMPNode> getMetaPathNodeChildren(){
        return metaPathNodeChildren;
    }

    public Map<Integer, IndexMPNode> getMetaPathNodeParent(){
        return metaPathNodeParent;
    }

    public void addKNodeChild(IndexKNode indexKNode){
        this.kNodesChildren.add(indexKNode);
    }


}
