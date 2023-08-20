package code.index.tree;

import code.index.basic.ConstructIACHIndex;
import code.util.CustomHashMap;

import java.util.*;

public class IndexTreeNode {
    private Set<Integer> vertexSet = new HashSet<>();
    private Set<Integer> allVertexSet = new HashSet<>();
//    private List<Double> value = new ArrayList<>();
    private List<IndexTreeNode> children = new ArrayList<>();
    private IndexTreeNode parent;
    private IndexKNode kParent;
    private Map<Integer, Double> nodeValueMap = new HashMap<>();
    private int level = 1;
    private boolean advancedVirtualFlag = false;
    private Map<Integer, Double> allNodeValueMap = new HashMap<>();
    private Map<Integer, List<IndexTreeNode>> allChildrenMap = new HashMap<>();  // 自该结点向下的全部的节点包含的顶点
    private Map<Integer, Map<Integer, Integer>> kLevelVerticesNumberMap = new CustomHashMap<>();
//    private ConstructIACHIndex constructIACHIndex=null;

    // 给定节点编号创建树节点
    public IndexTreeNode(int node) {
        this.vertexSet.add(node);
        this.children = new ArrayList<>();
        this.parent = null;
        this.nodeValueMap.putIfAbsent(node, 0.0);
    }

    // 给定节点编号和节点值创建树节点
    public IndexTreeNode(int node, double value) {
        this.vertexSet.add(node);
//        this.value.add(value);
        this.children = new ArrayList<>();
        this.parent = null;
        this.nodeValueMap.putIfAbsent(node, value);
    }

    // 给定节点编号和索引构造器创建树节点
    public IndexTreeNode(int node, ConstructIACHIndex constructIACHIndex) {
        this.vertexSet.add(node);
//        this.value.add(constructIACHIndex.getWeight()[node]);
        this.children = new ArrayList<>();
        this.parent = null;
        this.nodeValueMap.putIfAbsent(node, constructIACHIndex.getWeight()[node]);
    }

    // 给定一组节点编号和索引构造器创建树节点
    public IndexTreeNode(int[] nodes, ConstructIACHIndex constructIACHIndex){
        for (int node : nodes) {
            this.vertexSet.add(node);
            this.nodeValueMap.putIfAbsent(node, constructIACHIndex.getWeight()[node]);
        }
        this.children = new ArrayList<>();
        this.parent = null;

    }

    public IndexTreeNode(IndexTreeNode parent, IndexKNode kParent){
        this.kParent = kParent;
        this.parent = parent;
        this.level = parent.level + 1;
    }

    // 给定一组节点编号创建树节点
    public IndexTreeNode(int[] nodes) {
        for (int node : nodes) {
            this.vertexSet.add(node);
        }
        this.children = new ArrayList<>();
        this.parent = null;
    }

    // 给定一组节点编号、一组节点值创建树节点，布尔值用于判断是否已经是原始权重
    // 如果布尔值为True，代表传过来的是全部的权重，否则传过来的是已经一一对应的的权重
    public IndexTreeNode(int[] nodes, double[] values, boolean isOriginalWeights) {
        for (int node : nodes) {
            this.vertexSet.add(node);
        }
        if (isOriginalWeights) {
            for (int node : nodes) {
                this.nodeValueMap.putIfAbsent(node, values[node]);
            }
        } else {
            // 否则需要nodes中和values中的值一一对应
            assert nodes.length == values.length;
            for (int i = 0; i < nodes.length; i++) {
                this.nodeValueMap.putIfAbsent(nodes[i], values[i]);
            }
        }
        this.children = new ArrayList<>();
        this.parent = null;
    }

    // 用于为没有权重的节点补全权重
    public void implementValue(ConstructIACHIndex constructIACHIndex){
//        assert value.size() == 0;
        for(int node : vertexSet){
            nodeValueMap.putIfAbsent(node, constructIACHIndex.getWeight()[node]);
        }
    }

    public Set<Integer>getVertexSet(){
        return vertexSet;
    }



    public Map<Integer, Double> getAllNodeValueMap(){return allNodeValueMap;}

    public Set<Integer> getAllVertexSet(){
        return allVertexSet;
    }

    /**
     * @Description : 用在advanced索引中，先clear掉再把自己以下的全换成自己的，即用allVertexSet代替Vertexset
     */
    public void setVertexSet(){
        this.vertexSet.clear();
        this.vertexSet=this.allVertexSet;
    }

    public void setVertexSet(int[] nodes, ConstructIACHIndex constructIACHIndex){
        for (int node : nodes) {
            this.vertexSet.add(node);
            this.nodeValueMap.putIfAbsent(node, constructIACHIndex.getWeight()[node]);
        }
    }

    public List<Double> getValue(){
        List<Double>value = new ArrayList<>();
        for (Map.Entry<Integer, Double>entry: nodeValueMap.entrySet()){
            value.add(entry.getValue());
        }
        return value;
    }

    public double getNodeValue(int node){
        return nodeValueMap.get(node);
    }

    public List<IndexTreeNode> getChildren() {
        return children;
    }

    public IndexTreeNode getParent() {
        return parent;
    }

    public IndexKNode getKParent(){
        return kParent;
    }

    public void setParent(IndexTreeNode node, IndexTreeNode parent) {
        node.parent = parent;
    }

    public void setParent(IndexTreeNode parent) {
        this.parent = parent;
    }

    public void setKParent(IndexKNode kParent){
        this.kParent = kParent;
    }

    public void setKParent(IndexTreeNode node, IndexKNode kParent){
        node.kParent = kParent;
    }

    public Map<Integer, List<IndexTreeNode>> getAllChildrenMap(){
        return allChildrenMap;
    }

    public void addChild(IndexTreeNode child) {
        children.add(child);
        child.setParent(this);
    }

    public void addChildren(List<IndexTreeNode> children) {
        for (IndexTreeNode child : children) {
            addChild(child);
        }
    }

    public void insertChild(IndexTreeNode child, int index) {
        children.add(index, child);
        child.setParent(this);
    }

    public void insertChild(IndexTreeNode child, IndexTreeNode sibling) {
        int index = children.indexOf(sibling);
        insertChild(child, index);
    }

    public void removeChild(IndexTreeNode child) {
        children.remove(child);
        child.setParent(null);
    }

    public void insertLeft(IndexTreeNode node) {
        if (parent != null) {
            parent.insertChild(node, this);
        }
    }

    public void insertRight(IndexTreeNode node) {
        if (parent != null) {
            int index = parent.getChildren().indexOf(this);
            if (index != -1) {
                parent.insertChild(node, index + 1);
            }
        }
    }

    public void setLevel(int level){
        this.level = level;
    }

    public int getLevel(){
        return this.level;
    }

    public void setChild(IndexTreeNode indexTreeNode){
        this.children.add(indexTreeNode);
    }

    // 设置自己父节点的子结点为自身
    public void setParentChild(){
        this.parent.children.add(this);
    }

    public boolean getFlag(){
        return this.advancedVirtualFlag;
    }

    public void modifyFlag(boolean flag){
        advancedVirtualFlag = flag;
    }

    public Map<Integer, Double> getNodeValueMap(){
        return nodeValueMap;
    }

    public void setNodeValueMap(Map<Integer, Double> map){
        nodeValueMap.clear();
        nodeValueMap = map;
    }

    public void calAllChildren(IndexTreeNode node){
        for (IndexTreeNode indexTreeNode:this.children){
            List<IndexTreeNode> list = node.allChildrenMap.computeIfAbsent(indexTreeNode.level, k -> new ArrayList<>());
            list.add(indexTreeNode);
            indexTreeNode.calAllChildren(node);
        }
    }

    public double getMinValue(){
        double min = Double.MAX_VALUE;
        for (Map.Entry<Integer, Double>entry: nodeValueMap.entrySet()){
            if (entry.getValue() < min){
                min = entry.getValue();
            }
        }
        return min;
    }

    public double getMaxValue(){
        double max = Double.MIN_VALUE;
        for (Map.Entry<Integer, Double>entry: nodeValueMap.entrySet()){
            if (entry.getValue() > max){
                max = entry.getValue();
            }
        }
        return max;
    }

    public void modifyVertexSet(Set<Integer> set){
        this.vertexSet.removeAll(set);
    }

    public void modifyNodeValueMap(Set<Integer> set){
        for (int s:set){
            this.nodeValueMap.remove(s);
        }
    }

    public Map<Integer, Map<Integer, Integer>> getKLevelVerticesNumberMap(){
        return this.kLevelVerticesNumberMap;
    }

    public void setKLevelVerticesNumberMap(List<IndexTreeNode> possibleNodeList, int k){
        Map<Integer, Integer> innerMap = new HashMap<>();
        for (IndexTreeNode possibleIndexTreeNode:possibleNodeList){
            Set<Integer>possibleV = possibleIndexTreeNode.getVertexSet();
            if (possibleV.stream().mapToInt(item -> item).anyMatch(item -> vertexSet.contains(item))) {
                innerMap.put(possibleIndexTreeNode.getLevel(), possibleIndexTreeNode.getVertexSet().size());
            }
        }
        kLevelVerticesNumberMap.put(k, new TreeMap<>(innerMap));
        innerMap.clear();
        innerMap.put(this.level, this.vertexSet.size());
        kLevelVerticesNumberMap.put(k+1, innerMap);

        for (Map.Entry<Integer, Map<Integer, Integer>> entry : kLevelVerticesNumberMap.entrySet()) {
            Map<Integer, Integer> innerSortedMap = new TreeMap<>(entry.getValue());
            entry.setValue(innerSortedMap);
        }

    }

    /**
     * @Description : 自底向上找当前节点的第一个具有兄弟节点的先祖；即找到第一个有多个孩子的顶点的孩子且是当前的祖先的节点
     */
    public IndexTreeNode getLeaderNode(){
        IndexTreeNode node = this.getParent();
        if (node.children.size() > 1){
            return this;
        }
        while (node.getParent().children.size() == 1){
            node = node.getParent();
            if (node.getParent() == null)
                break;
        }
        return node;
    }

    public void setAllVertexAndValueMap(){
        calAllChildren(this);
        Collection<List<IndexTreeNode>> nodesCollection = this.getAllChildrenMap().values();
        List<IndexTreeNode> nodes = new ArrayList<>();
        for (List<IndexTreeNode> nodesList : nodesCollection) {
            nodes.addAll(nodesList);
        }
        nodes.add(this);
        for (IndexTreeNode node : nodes){
            allVertexSet.addAll(node.vertexSet);
            for (Map.Entry<Integer, Double>entry : node.nodeValueMap.entrySet()) {
                allNodeValueMap.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
    }

//    /**
//     * @Description : 给当前顶点的allNodeValueMap存以下全部顶点的nodeValueMap
//     */
//    public void setAllNodeValueMap(){
//        // 先放自己的
//        for (Map.Entry<Integer, Double> entry : this.getNodeValueMap().entrySet()) {
//            this.allNodeValueMap.putIfAbsent(entry.getKey(), entry.getValue());
//        }
//
//        // 再放孩子的
//        List<IndexTreeNode> allChildren = new ArrayList<>();
//        Collection<List<IndexTreeNode>> collection = this.getAllChildrenMap().values();
//        for (List<IndexTreeNode> item : collection){
//            allChildren.addAll(item);
//        }
//        for (IndexTreeNode indexTreeNode : allChildren){
//            for (Map.Entry<Integer, Double> entry : indexTreeNode.getNodeValueMap().entrySet()) {
//                this.allNodeValueMap.putIfAbsent(entry.getKey(), entry.getValue());
//            }
//        }
//    }
}
