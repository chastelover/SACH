package code.index.tree;

import code.MyException;
import code.online.MetaPath;
import code.util.Config;
import code.util.DataReader;
import code.util.Dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexTreeParser {
    private IndexTree indexTree;

    public IndexTreeParser() {
        this.indexTree = new IndexTree();
    }

    public IndexTree getIndexTree() {
        return indexTree;
    }

    /**
     *
     * @param filePath
     * @param type ： 类别参数，分七种情况解析数据，按照dataset文件夹中的顺序对应
     * @return ： 返回解析完的索引树
     * @throws Exception
     */
    public IndexTree parseFile(String filePath, String type) throws Exception {
        type = type.toLowerCase();
        Dictionary dictionary = new Dictionary();
        DataReader dataReader = null;
        if (type.equals("dblp")) {
            dictionary.loadMappingsFromFile(Config.dblpReadme);
            dataReader = new DataReader(Config.dblpGraph, Config.dblpVertex, Config.dblpEdge, Config.dblpVertexWeight);
        }
        if (type.equals("foursquare")) {
            dictionary.loadMappingsFromFile(Config.FsqReadme);
            dataReader = new DataReader(Config.FsqGraph, Config.FsqVertex, Config.FsqEdge, Config.FsqVertexWeight);
        }
        if (type.equals("imdb")) {
            dictionary.loadMappingsFromFile(Config.IMDBReadme);
            dataReader = new DataReader(Config.IMDBGraph, Config.IMDBVertex, Config.IMDBEdge, Config.IMDBVertexWeight);
        }
        if (type.equals("pubmed")) {
            dictionary.loadMappingsFromFile(Config.PubMedReadme);
            dataReader = new DataReader(Config.PubMedGraph, Config.PubMedVertex, Config.PubMedEdge, Config.PubMedVertexWeight);
        }
        if (type.equals("smalldblp")) {
            dictionary.loadMappingsFromFile(Config.smallDBLPReadme);
            dataReader = new DataReader(Config.smallDBLPGraph, Config.smallDBLPVertex, Config.smallDBLPEdge, Config.smallDBLPVertexWeight);
        }
        if (type.equals("likedblp")) {
            dictionary.loadMappingsFromFile(Config.likeDBLPReadme);
            dataReader = new DataReader(Config.likeDBLPGraph, Config.likeDBLPVertex, Config.likeDBLPEdge, Config.likeDBLPVertexWeight);
        }
        if (type.equals("likedblp2")) {
            dictionary.loadMappingsFromFile(Config.likeDBLPReadme2);
            dataReader = new DataReader(Config.likeDBLPGraph2, Config.likeDBLPVertex2, Config.likeDBLPEdge2, Config.likeDBLPVertexWeight2);
        }
        if (dataReader == null){
            throw new MyException("type参数错误，请输入[1,7]的整数");
        }
        dictionary.completeMap();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            List<IndexMPNode> indexMPNodeList = new ArrayList<>();
            List<IndexKNode> indexKNodeList = new ArrayList<>();
            List<IndexTreeNode> indexTreeNodeList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                // 解析每一行数据
                IndexMPNode indexMPNode = null;IndexKNode indexKNode = null;

                // 若不是第一次遍历到indexMPNode或IndexKNode，则以下两个集合都不为空
                if (!indexMPNodeList.isEmpty()) {
                    indexMPNode = indexMPNodeList.get(indexMPNodeList.size() - 1);
                }
                if (!indexKNodeList.isEmpty()){
                    indexKNode = indexKNodeList.get(indexKNodeList.size() - 1);
                }

                // 若到了文件末尾，把还没有处理的IndexKNode加到IndexMPNode下
                if (line.contains(Config.indexTreeEnd)){
                    if (!indexTreeNodeList.isEmpty() && indexKNode != null){
                        for (IndexTreeNode indexTreeNode: indexTreeNodeList){
                            indexTreeNode.calAllChildren(indexTreeNode);
                            indexKNode.addChildKNode(indexTreeNode);
                        }
                    }
                }

                // 处理文件中的indexMPNode
                if (line.contains(Config.indexMPNode)){
                    indexMPNode = parseMetaPath(line, dictionary);
                    indexMPNodeList.add(indexMPNode);
                    indexTree.addChild(indexMPNode);
                    continue;
                }

                // 处理文件中的indexKNode
                if (line.contains(Config.indexKNode)){
                    if (indexMPNode==null){
                        throw new MyException("解析到IndexKNode时IndexMPNode还为空");
                    }
                    if (!indexTreeNodeList.isEmpty() && indexKNode != null){
                        for (IndexTreeNode indexTreeNode: indexTreeNodeList){
                            indexTreeNode.calAllChildren(indexTreeNode);
                            indexKNode.addChildKNode(indexTreeNode);
                        }
                    }  // 截至这里，上一颗k子树已经读完了，处理先前的k子树的indexKNode，再获取新的indexKNode

                    indexKNode = parseK(line, indexMPNode);
                    if (indexKNode == null)
                        throw new MyException("解析k错误");
                    indexKNodeList.add(indexKNode);
                    indexMPNode.setKNodesChildren(indexKNode);
                    // 以上设置解析indexKNode自身和其父子关系


                    indexTreeNodeList.clear();  // 到IndexKNode行说明一棵子树建立完全了
                    continue;
                }
                // 处理文件中的indexTreeNode
                if (line.contains(Config.indexTreeNode)){
                    if (indexKNode==null){
                        throw new MyException("解析到IndexTreeNode时IndexKNode还为空");
                    }
                    IndexTreeNode indexTreeNode = parseIndexTreeNode(line, dataReader);
                    assert indexTreeNode != null;
                    indexTreeNode.setKParent(indexKNode);


                    int indent = line.indexOf('└'); // 打印索引时一级缩进5个位置，找到第一个'└'的位置，除以5代表在树中的level
//                    System.out.println(indent);
                    indexTreeNode.setLevel(indent/5);

                    if (!indexTreeNodeList.isEmpty()) {
                        IndexTreeNode lastNode = indexTreeNodeList.get(indexTreeNodeList.size() - 1);
                        if (indexTreeNode.getLevel() == lastNode.getLevel() + 1){
                            lastNode.setChild(indexTreeNode);
                            indexTreeNode.setParent(lastNode);
                        }
                        else {
                            for (int i = indexTreeNodeList.size() - 1; i >= 0; i--) {
                                if (indexTreeNodeList.get(i).getLevel() == indexTreeNode.getLevel() - 1) {
                                    indexTreeNodeList.get(i).setChild(indexTreeNode);
                                    indexTreeNode.setParent(indexTreeNodeList.get(i));
                                    break;
                                }
                            }
                        }
                    }

                    indexTreeNodeList.add(indexTreeNode);
                }
            }
        }
//        indexTree.printIndexTree("");
//        IndexTreePrinter indexTreePrinter = new IndexTreePrinter("temp");
//        indexTreePrinter.printIndexTree("", indexTree);
//        indexTreePrinter.close();
        return indexTree;
    }

    /**
     * @Description : 解析元路径并创建IndexMPNode节点
     * @param line
     * @param dictionary
     * @return
     * @throws IOException
     */
    private IndexMPNode parseMetaPath(String line, Dictionary dictionary) throws IOException {
        String str = Config.indexMPNode;
        String regex = "\\[(.*?)\\]";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);


        if (matcher.find()) {
            String result = matcher.group(1);
            String[] vertex = result.split(",");
            for (int i=0; i < vertex.length; i++){
                vertex[i] = vertex[i].trim();
            }
            MetaPath metaPath = new MetaPath(vertex, dictionary);
            return new IndexMPNode(metaPath);
        }
        System.out.println("解析MetaPath失败");
        return null;
    }

    private IndexKNode parseK(String line, IndexMPNode indexMPNode){
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(line);
        int number = 0;
        // 查找匹配的数字
        while (matcher.find()) {
            String numberString = matcher.group();
            number = Integer.parseInt(numberString);
//            System.out.println("匹配到的数字：" + number);
        }
        if (number <= 0){
            return null;
        }
        return new IndexKNode(number, indexMPNode);
    }

    /**
     * @Description : 根据键值对或者节点名创建IndexTreeNode节点，若仅给节点名，要读源数据集去拿节点权重
     * @param line ： 读到的一行
     * @param dataReader ： 自定义的DataReader类，包含读哪个数据集的信息
     * @return ： 创建一个IndexTreeNode节点
     */
    private IndexTreeNode parseIndexTreeNode(String line, DataReader dataReader) {
        // 定义正则表达式
        String regex = "[{\\[]((\\d+)(=\\d+\\.\\d+)?(,\\s*)?)+[}\\]]";

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(regex);

        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String match = matcher.group();
            // 匹配了如IndexTreeNode: {5=5.0, 6=6.0, 7=7.0, 8=8.0, 9=9.0}的字符串
            if (match.startsWith("{")) {
                List<Integer> vertexList = new ArrayList<>();
                List<Double> weightList = new ArrayList<>();
                String[] keyValuePairs = match.substring(1, match.length() - 1).split(",\\s*");
                for (String pair : keyValuePairs) {
                    String[] parts = pair.split("=");
                    int key = Integer.parseInt(parts[0]);
                    double value = Double.parseDouble(parts[1]);
                    vertexList.add(key);
                    weightList.add(value);
                }
                int[] nodes = new int[vertexList.size()];
                double[] weights = new double[weightList.size()];
                assert vertexList.size() == weightList.size();
                for (int i = 0; i < vertexList.size(); i++) {
                    nodes[i] = vertexList.get(i);
                    weights[i] = weightList.get(i);
                }
                return new IndexTreeNode(nodes, weights, false);
            }
            // 匹配了如IndexTreeNode: [5,6,7,8,9]的字符串
            if (match.startsWith("[")) {
                String[] elements = match.substring(1, match.length() - 1).split(",\\s*");
                int[] nodes = new int[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    nodes[i] = Integer.parseInt(elements[i]);
                }
                double[] weights = dataReader.readVertexWeight();
                return new IndexTreeNode(nodes, weights, false);
            }
        }

        System.out.println("IndexTreeNode节点有误，节点未能以{int=weight}或[int,int]形式：" + line);
        return null;
    }
}

