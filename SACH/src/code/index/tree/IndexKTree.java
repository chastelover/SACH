package code.index.tree;

import java.util.*;

public class IndexKTree {
    private final IndexKNode root;  // 根节点都是IndexKNode类型

    public IndexKTree(IndexKNode indexKNode) {
        root = indexKNode;
    }

    public IndexKNode getRoot() {
        return root;
    }

    /* 已废除
    public void printTree(){
        IndexKNode root = this.root;
        if (root == null){
            System.out.println("空树");
            return;
        }
        System.out.println("根节点：" + root.getK());
        System.out.println("根节点的孩子节点：");
//        System.out.println(root.getChildren());
        for (Map.Entry<Integer, List<IndexTreeNode>>entry: root.getChildren().entrySet()) {
            for (IndexTreeNode indexTreeNode: entry.getValue()){
                System.out.println(indexTreeNode.getNodeValueMap());
            }
//            System.out.println(entry.getValue().getNodeValueMap());
        }
    }  */

    public static void printIndexKTree(String prefix, IndexKNode root, boolean isAdvanced){

        if (root == null){
            System.out.println("空树");
        }
        System.out.println((prefix +  "├── ") + "IndexKNode: k = " + root.getK());
        List<IndexTreeNode>firstChild = root.getChildren().get(1);
        for (IndexTreeNode indexTreeNode:firstChild){
            if (!isAdvanced)
                printIndexTree(indexTreeNode, prefix+"   ");
            else printAdvancedIndexTree(indexTreeNode, prefix+"   ", false);
        }
    }

    private static void printIndexTree(IndexTreeNode node, String prefix) {
        node.calAllChildren(node);
//        System.out.println(prefix + "└──── Node: " + node.getVertexSet());  // 输出带属性的则替换为node.getNodeValueMap());
        System.out.println(prefix + "└──── IndexTreeNode: " + node.getNodeValueMap());
        List<IndexTreeNode> children = node.getChildren();
        int lastChildIndex = children.size() - 1;

        for (int i = 0; i <= lastChildIndex; i++) {
            IndexTreeNode child = children.get(i);
            String newPrefix = prefix + (i == lastChildIndex ? "     " : "│    ");
            printIndexTree(child, newPrefix);
        }
    }

    private static void printAdvancedIndexTree(IndexTreeNode node, String prefix, boolean print) {
        node.calAllChildren(node);
//        System.out.println(prefix + "└──── Node: " + node.getVertexSet());  // 输出带属性的则替换为node.getNodeValueMap());
        if (node.getFlag()){
            System.out.println(prefix + "└···· VirtualIndexTreeNode: " + node.getVertexSet());
        }
        else {
            if (print) {
                if (node.getKLevelVerticesNumberMap().size() != 0)
                    System.out.println(prefix + "└──── IndexTreeNode: " + node.getNodeValueMap() + "  " + node.getKLevelVerticesNumberMap());
                else
                    System.out.println(prefix + "└──── IndexTreeNode: " + node.getNodeValueMap());
            }
        }
        List<IndexTreeNode> children = node.getChildren();
        int lastChildIndex = children.size() - 1;

        for (int i = 0; i <= lastChildIndex; i++) {
            IndexTreeNode child = children.get(i);
            String newPrefix = prefix + (i == lastChildIndex ? "     " : "│    ");
            print = child.getNodeValueMap().size() != 0;
            printAdvancedIndexTree(child, newPrefix, print);
        }
    }

    /**
     * @Description : 递归获取root下的孩子节点并打印
     * @param root
     */
    public static void printCommunity(IndexKNode root, boolean printNode) {
        if (root == null){
            System.out.println("执行printCommunity函数时，传入的root为空");
            return;
        }
        List<IndexTreeNode>firstChild = root.getChildren().get(1);
        Map<Integer, Double>resultMap = new HashMap<>();
        for (IndexTreeNode indexTreeNode:firstChild){
            catchAllNode(indexTreeNode, resultMap);
        }
        TreeMap<Integer, Double>sortedMap = new TreeMap<>(resultMap);
        if (printNode) {
            System.out.println(sortedMap);
        }
        System.out.println(resultMap);
    }

    private static void catchAllNode(IndexTreeNode node, Map<Integer, Double>map){
        map.putAll(node.getNodeValueMap());
        for (IndexTreeNode childNode: node.getChildren()){
            catchAllNode(childNode, map);
            }
        }

    /**
     *
     * @param root : 一个顶点，可以是IndexKNode，也可以是IndexTreeNode，输出其下所有IndexTreeNode类型的子节点
     * @param isKNode ： 是IndexKNode类型，若为false则是IndexTreeNode类型
     * @Description : 这个函数实现了输入一个IndexKNode或IndexTreeNode，输出其下所有IndexTreeNode类型的子节点
     */
    public static void printCommunity1(Object root, boolean isKNode, boolean printItSelf, boolean printNode){
        Map<Integer, Double>resultMap = new HashMap<>();
        if (isKNode) {
            IndexKNode root1 = (IndexKNode) root;
            for (Map.Entry<Integer, List<IndexTreeNode>> entry : root1.getChildren().entrySet()) {
                for (IndexTreeNode indexTreeNode : entry.getValue()) {
                    resultMap.putAll(indexTreeNode.getNodeValueMap());
//                    System.out.print(indexTreeNode.getNodeValueMap());
                }
            }
        }
        else {
            IndexTreeNode root1 = (IndexTreeNode) root;
            if (printItSelf){
                resultMap.putAll(root1.getNodeValueMap());
//                System.out.print(root1.getNodeValueMap());
            }
            for (Map.Entry<Integer, List<IndexTreeNode>> entry : root1.getAllChildrenMap().entrySet()) {
                for (IndexTreeNode indexTreeNode : entry.getValue()) {
                    resultMap.putAll(indexTreeNode.getNodeValueMap());
//                    System.out.print(indexTreeNode.getNodeValueMap());
                }
            }
        }
        TreeMap<Integer, Double>sortedMap = new TreeMap<>(resultMap);
        if (printNode) {
            System.out.println(sortedMap);
        }
        System.out.print(resultMap);
    }

    /**
     * @Description : 由于索引树自上而下顶点的排列是按照属性值增大的特性，所以这个函数实现了在一颗索引树内寻找属性值合适的子树
     * @Description : 如属性值为1、k=1，找到了存储v1的树节点、存储v5的树节点，返回v1、v5组成的List，以该两个节点为root的子树全部满足属性约束
     * @param node ： 传入的顶点，可以是IndexKNode类型或IndexTreeNode类型，在第三个bool参数中设置相应的值
     * @param weight ： 权重
     * @param isKNode ： 是否是K顶点
     * @return ： 返回一个List，包含了所有满足属性约束的子树的根节点
     */
    public static List<IndexTreeNode>findWeightFitNodes(Object node, double weight, boolean isKNode){
        List<IndexTreeNode>nodeList = new ArrayList<>();
        if (isKNode){
            IndexKNode indexKNode = (IndexKNode)node;
            for (IndexTreeNode indexTreeNode : indexKNode.getChildren().get(1)){
                nodeList.addAll(findWeightFitNodes(indexTreeNode, weight, false));
            }
        }
        else {
            IndexTreeNode indexTreeNode = (IndexTreeNode)node;
            if (indexTreeNode.getMinValue() >= weight) {
                nodeList.add(indexTreeNode);
            } else {
                for (IndexTreeNode indexTreeNode1 : indexTreeNode.getChildren()) {
                    nodeList.addAll(findWeightFitNodes(indexTreeNode1, weight, false)) ;
                }
            }
        }
        Set<IndexTreeNode>nodeSet = new HashSet<>(nodeList);
        return new ArrayList<>(nodeSet);
    }

    /**
     * @Description : 旨在找到第一颗子树
     * @param node ： 传入的顶点，可以是IndexKNode类型或IndexTreeNode类型，在第三个bool参数中设置相应的值
     * @param weight ： 权重
     * @param isKNode ： 是否是K顶点
     * @return ：与上面函数不同的是，返回找到的第一棵子树的根节点
     */
    public static IndexTreeNode findWeightFitNode(Object node, double weight, boolean isKNode){
        if (isKNode){
            IndexKNode indexKNode = (IndexKNode)node;
            for (Map.Entry<Integer, List<IndexTreeNode>>entry:indexKNode.getChildren().entrySet()) {
                for (IndexTreeNode indexTreeNode : entry.getValue()) {
                   return findWeightFitNode(indexTreeNode, weight, false);
                }
            }

        }
        else {
            IndexTreeNode indexTreeNode = (IndexTreeNode)node;
            if (indexTreeNode.getMaxValue() >= weight) {
                return indexTreeNode;
            }
            else {
                for (IndexTreeNode indexTreeNode1 : indexTreeNode.getChildren()) {
                    return findWeightFitNode(indexTreeNode1, weight, false);
                }
            }
        }
        return null;
    }

    /**
     * @Description : 在一颗索引树内寻找属性值包含限定顶点的子树
     * @param node ： 传入的顶点，可以是IndexKNode类型或IndexTreeNode类型，在第三个bool参数中设置相应的值
     * @param queryID ： 查询顶点编号
     * @param isKNode ： 是否是K顶点
     * @return ：与上面函数不同的是，返回找到的第一棵子树的根节点
     */
    public static List<IndexTreeNode>findQueryIDFitNodes(Object node, int queryID, boolean isKNode){
        List<IndexTreeNode>nodeList = new ArrayList<>();
        if (isKNode){
            IndexKNode indexKNode = (IndexKNode)node;
            for (IndexTreeNode indexTreeNode : indexKNode.getChildren().get(1)){
                nodeList.addAll(findQueryIDFitNodes(indexTreeNode, queryID, false));
            }
        }
        else {
            IndexTreeNode indexTreeNode = (IndexTreeNode)node;
            if (indexTreeNode.getVertexSet().contains(queryID)) {
                nodeList.add(indexTreeNode);
            } else {
                for (IndexTreeNode indexTreeNode1 : indexTreeNode.getChildren()) {
                    nodeList.addAll(findQueryIDFitNodes(indexTreeNode1, queryID, false)) ;
                }
            }
        }
        Set<IndexTreeNode>nodeSet = new HashSet<>(nodeList);
        return new ArrayList<>(nodeSet);
    }
}
