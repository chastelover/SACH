package code.index.Query;

import code.index.tree.IndexKNode;
import code.index.tree.IndexKTree;
import code.index.tree.IndexMPNode;
import code.index.tree.IndexTree;
import code.online.MetaPath;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Liuyanghao
 * @Aim : 为了方便索引查询，将查询节点和k指约束封装成一个类
 * @Date : 2023-06-13 15:52
 * @Description : 支持给元路径+k和给元路径+k+查询节点的索引查询
 */
public class QueryIndex {
    private int queryQid;;
    private int k;
    private MetaPath metaPath = null;
    private double weight;

    // 给定元路径、查询节点和k指约束的索引查询
    public QueryIndex(MetaPath metaPath, int queryQid, int k) {
        this.metaPath = metaPath;
        this.queryQid = queryQid;
        this.k = k;
    }

    // 仅给定元路径和k指约束的索引查询
    public QueryIndex(MetaPath metaPath, int k){
        this.metaPath = metaPath;
        this.k = k;
    }

    public QueryIndex(MetaPath metaPath,int k, double weight){
        this.metaPath = metaPath;
        this.weight = weight;
        this.k = k;
    }

    public int getQueryQid() {
        return queryQid;
    }

    public void setQueryQid(int queryQid) {
        this.queryQid = queryQid;
    }

    public MetaPath getMetaPath() {
        return metaPath;
    }

    public void setMetaPath(MetaPath metaPath) {
        this.metaPath = metaPath;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "queryIndex{" +
                "metaPath=" + metaPath +
                "queryQid=" + queryQid +
                ", k=" + k +
                '}';
    }

    public Object getIndexKTree(int k, IndexTree indexTree, boolean needTree){
        IndexMPNode newIndexMPNode = null;
        for (IndexMPNode indexMPNode: indexTree.getChildren()){
            if (indexMPNode.getMetaPath().equals(metaPath)){
                newIndexMPNode = indexMPNode;
                break;
            }
        }
        if (newIndexMPNode == null){
            System.out.println("获取KTree失败，可能是索引建立问题");
            return null;
        }
        for (IndexKNode indexKNode: newIndexMPNode.getKNodesChildren()){
            if (indexKNode.getK() == k){
                if (needTree) {
                    return new IndexKTree(indexKNode);
                }
                else {
                    return indexKNode;
                }
            }
        }
        System.out.println("获取KTree失败，k值不合法");
        return null;
    }

    public List<IndexKNode> getAdvancedKNodesList(int k,  IndexTree indexTree){
        IndexMPNode newIndexMPNode = null;
        List<IndexKNode> list = new ArrayList<>();
        for (IndexMPNode indexMPNode: indexTree.getChildren()){
            if (indexMPNode.getMetaPath().equals(metaPath)){
                newIndexMPNode = indexMPNode;
                break;
            }
        }
        if (newIndexMPNode == null){
            System.out.println("获取KTree失败，可能是索引建立问题");
            return null;
        }
        for (IndexKNode indexKNode: newIndexMPNode.getKNodesChildren()){
            if (indexKNode.getK() - k <= 5 && indexKNode.getK() > k){
                list.add(indexKNode);
            }
        }
        return list;
    }

    public IndexKNode getAdvancedKNode(int q, IndexTree indexTree){
        IndexMPNode newIndexMPNode = null;
        for (IndexMPNode indexMPNode: indexTree.getChildren()){
            if (indexMPNode.getMetaPath().equals(metaPath)){
                newIndexMPNode = indexMPNode;
                break;
            }
        }
        if (newIndexMPNode == null){
            System.out.println("获取KTree失败，可能是索引建立问题");
            return null;
        }
        for (IndexKNode indexKNode: newIndexMPNode.getKNodesChildren()){
            if (indexKNode.getObVertices().contains(q)){
                return indexKNode;
            }
        }
        System.out.println("Advanced索引没有找到对的KNode");
        return null;
    }
}
