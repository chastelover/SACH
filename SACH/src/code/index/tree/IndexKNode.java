package code.index.tree;

import code.MyException;
import code.online.MetaPath;

import java.util.*;

public class IndexKNode {
    private int k;
    private MetaPath metaPath; // 倒排存储当前顶点所属的metaPath
    private int kMax=-1 ; // 存储一下当前元路径下最大的k值，该值或许获取不到，初值置为-1即可
    private List<Integer> Vertices = new ArrayList<>();
    private List<Integer> ObVertices = new ArrayList<>();
    private HashMap<Integer, List<IndexTreeNode>> children = new HashMap<>();
    private Set<IndexMPNode>parentMPNodeSet = new HashSet<>(); // 暂时不用Set就够了，为了优化算法做准备

    public IndexKNode(int k, MetaPath metaPath) {
        this.k = k;
        this.metaPath = metaPath;
    }

    public IndexKNode(int k, IndexMPNode indexMPNode){
        this.k = k;
        this.metaPath = indexMPNode.getMetaPath();
        this.parentMPNodeSet.add(indexMPNode);

    }

    public IndexKNode(int k, MetaPath metaPath, int kMax) {
        this.k = k;
        this.metaPath = metaPath;
        this.kMax = kMax;
    }

    // 后来修改了Children形式，所以这个构造函数不再使用
    /*
    public IndexKNode(int k, MetaPath metaPath, Set<Integer> vertexSet) {
        this.k = k;
        this.metaPath = metaPath;
        this.children.putIfAbsent(k, vertexSet);
    }*/

    // 后来修改了Children形式，所以这个构造函数不再使用
    /*
    public IndexKNode(int k, MetaPath metaPath, HashMap<Integer, Set<Integer>> children) {
        this.k = k;
        this.metaPath = metaPath;
        this.children = children;
    }*/

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getKMax(){
        return kMax;
    }

    public void setKMax(int kMax){
        this.kMax = kMax;
    }

    public MetaPath getMetaPath() {
        return metaPath;
    }

    public HashMap<Integer, List<IndexTreeNode>> getChildren() {
        return children;
    }

    public void addChildKNode(IndexTreeNode indexTreeNode) throws Exception {
        int key = indexTreeNode.getLevel();
        children.compute(key, (k, v) -> {
            if (v == null){
                v = new ArrayList<>();
            }
            v.add(indexTreeNode);
            return v;
        });
        if (indexTreeNode.getKParent() == null) {
            indexTreeNode.setKParent(this);
        }
        if (indexTreeNode.getKParent() != this) {
            System.out.println("error");
            throw new MyException("K一级的父节点不是当前节点");
        }
    }

    public static List<IndexTreeNode> getAllIndexTreeNodeBelow(IndexKNode indexKNode, int except){
        List<IndexTreeNode>result = new ArrayList<>();
        for (int key:indexKNode.getChildren().keySet()){
            if (key<=except)
                continue;
            result.addAll(indexKNode.getChildren().get(key));
        }
        return result;
    }

    public List<IndexTreeNode> getAllTreeNodes(){
        List<IndexTreeNode>list = new ArrayList<>();
       Collection<List<IndexTreeNode>> value = children.values();
       for (List<IndexTreeNode>item:value) {
           list.addAll(item);
       }
       return list;
    }

    public void setDirectVertices(){
        // 取直接孩子
        List<IndexTreeNode>directChildren = children.get(1);
        for (IndexTreeNode child:directChildren){
            child.setAllVertexAndValueMap();
        }
    }

    public static List<IndexTreeNode> setLeaderVerticesAndGetLeader(IndexKNode indexKNode) throws MyException {
        List<IndexTreeNode> allLeaderNodes = new ArrayList<>();
        List<IndexTreeNode> temp = new ArrayList<>();
        List<IndexTreeNode>leafNodes = new ArrayList<>();
        HashMap<Integer, List<IndexTreeNode>> allChildren = indexKNode.getChildren();
        for (List<IndexTreeNode> indexTreeNodeList:allChildren.values()) {
            temp.addAll(indexTreeNodeList);
        }

        for (IndexTreeNode indexTreeNode : temp){
            if (indexTreeNode==null) continue;
            if (indexTreeNode.getChildren() == null || indexTreeNode.getChildren().size() == 0)
                leafNodes.add(indexTreeNode);
        }

        for (IndexTreeNode indexTreeNode : leafNodes){
            try {
                IndexTreeNode leader = indexTreeNode.getLeaderNode();
                leader.calAllChildren(leader);
                leader.setAllVertexAndValueMap();
                allLeaderNodes.add(leader);
            }
            catch (Exception e){
                int k = indexKNode.k;
                throw new MyException((String.valueOf(k)));
            }

        }
        return allLeaderNodes;
    }


    public static void processAdvancedKNode(IndexKNode indexKNodeLast, IndexKNode indexKNode) throws MyException {
        Set<Integer> resultVertices = new HashSet<>();
        List<IndexTreeNode> allLeaders = setLeaderVerticesAndGetLeader(indexKNodeLast);

        // 获取IndexKNode下全部的顶点
        List<IndexTreeNode> result = getAllIndexTreeNodeBelow(indexKNode, 0);
        List<IndexTreeNode> resultLast = getAllIndexTreeNodeBelow(indexKNodeLast, 0);

        for (IndexTreeNode indexTreeNode : result){
            resultVertices.addAll(indexTreeNode.getVertexSet());
            List<IndexTreeNode> possibleNodeList = getAllIndexTreeNodeBelow(indexKNodeLast, indexTreeNode.getLevel() - 1);
            indexTreeNode.setKLevelVerticesNumberMap(possibleNodeList, indexKNodeLast.k);
        }

        for (IndexTreeNode indexTreeNode:resultLast) {
//            System.out.println(indexTreeNode.getVertexSet().retainAll(resultVerticesLast));

//            System.out.println(indexTreeNode.getKLevelVerticesNumberMap());
            Set<Integer> vertices = new HashSet<>(indexTreeNode.getVertexSet());
            for (int v:vertices){
                if (resultVertices.contains(v)){
                    indexTreeNode.modifyVertexSet(resultVertices);
                    indexTreeNode.modifyNodeValueMap(resultVertices);
                    if (allLeaders.contains(indexTreeNode)){
                        indexTreeNode.setNodeValueMap(indexTreeNode.getAllNodeValueMap());
                        indexTreeNode.setVertexSet();
                        indexTreeNode.modifyFlag(true);
                    }
                }
            }
        }
//        indexKNodeLast.calObVertices();
    }
    
    public void calVertices(){
        List<Integer> allVertices = new ArrayList<>();
        for (IndexTreeNode indexTreeNode: getAllIndexTreeNodeBelow(this, 0)){
            allVertices.addAll(indexTreeNode.getVertexSet());
        }
        Vertices = allVertices;
    }

    public List<Integer>getVertices(){
        return Vertices;
    }

    public void calObVertices(){
        List<Integer> allVertices = new ArrayList<>();
        for (IndexTreeNode indexTreeNode: getAllIndexTreeNodeBelow(this, 0)){
            allVertices.addAll(indexTreeNode.getVertexSet());
        }
        ObVertices = allVertices;
    }

    public List<Integer>getObVertices(){
        return ObVertices;
    }
}
