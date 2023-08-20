package code.index.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static code.index.tree.IndexKTree.printIndexKTree;

public class IndexTree {
    private List<IndexMPNode> indexMPNodesChildren = new ArrayList<>();
    private int countMetaPath = 0;

    public IndexTree(){

    }

    public List<IndexMPNode> getIndexMPNodesChildren(){
        return this.indexMPNodesChildren;
    }

    public IndexTree(List<IndexMPNode> indexMPNodes) {
        this.indexMPNodesChildren = indexMPNodes;
    }

    public IndexTree(IndexMPNode indexMPNode){
        this.indexMPNodesChildren.add(indexMPNode);
    }

    public int calCountMetaPath(){
        this.countMetaPath = indexMPNodesChildren.size();
        return this.countMetaPath;
    }

    public void addChild(IndexMPNode indexMPNode){
        this.indexMPNodesChildren.add(indexMPNode);
    }

    public List<IndexMPNode> getChildren() {
        return indexMPNodesChildren;
    }

    public void printIndexTree(String prefix){
        System.out.println(prefix +  "-----------------------------IndexTreeRoot--------------------------");
        for (IndexMPNode indexMPNode: this.indexMPNodesChildren){
            System.out.println(prefix + "└──── IndexMPNode: " + indexMPNode.getMetaPath().printVertexName());
            for (IndexKNode indexKNode: indexMPNode.getKNodesChildren()){
                printIndexKTree(prefix + "     ", indexKNode, true);
            }
//            System.out.println();
        }
        System.out.print(prefix +  "-----------------------------IndexTreeEnd--------------------------");
    }


}
