package code.index.tree;

import code.util.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @Author : liuyanghao
 * @Date : 2023.06.15
 * @Description : 索引树打印到文件，文件以IACHIndex_时间戳.txt命名，打印到文件中时保留树格式
 */
public class IndexTreePrinter {
    private PrintWriter writer;
    private final static String fileDir = Config.outRoot;

    public IndexTreePrinter(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            writer = new PrintWriter(fileWriter, true); // 设置自动刷新
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void println(Object obj){
        writer.println(obj);
    }

    public void printIndexTree(String prefix, IndexTree indexTree) {
        writer.println(prefix + "-----------------------------IndexTreeRoot--------------------------");
        for (IndexMPNode indexMPNode : indexTree.getIndexMPNodesChildren()) {
            writer.println(prefix + "└──── IndexMPNode: " + indexMPNode.getMetaPath().printVertexName());
            for (IndexKNode indexKNode : indexMPNode.getKNodesChildren()) {
                printIndexKTree(prefix + "     ", indexKNode, true);
            }
        }
        writer.print(prefix +  "-----------------------------IndexTreeRoot--------------------------");
    }

    public void printIndexKTree(String prefix, IndexKNode root, boolean isAdvanced) {
        if (root == null) {
            writer.println("空树");
        }
        writer.println((prefix + "├── ") + "IndexKNode: k = " + root.getK());
        List<IndexTreeNode> firstChild = root.getChildren().get(1);
        for (IndexTreeNode indexTreeNode : firstChild) {
            if (!isAdvanced)
                printIndexTree(indexTreeNode, prefix+"   ");
            else printAdvancedIndexTree(indexTreeNode, prefix+"   ", false);
        }
    }

    private void printIndexTree(IndexTreeNode node, String prefix) {
        node.calAllChildren(node);
        writer.println(prefix + "└──── IndexTreeNode: " + node.getNodeValueMap());
        List<IndexTreeNode> children = node.getChildren();
        int lastChildIndex = children.size() - 1;

        for (int i = 0; i <= lastChildIndex; i++) {
            IndexTreeNode child = children.get(i);
            String newPrefix = prefix + (i == lastChildIndex ? "     " : "│    ");
            printIndexTree(child, newPrefix);
        }
    }

    private void printAdvancedIndexTree(IndexTreeNode node, String prefix, boolean print) {
        node.calAllChildren(node);
        if (print) {
            if (node.getKLevelVerticesNumberMap().size() !=0 )
                writer.println(prefix + "└──── IndexTreeNode: " + node.getNodeValueMap() + "  " + node.getKLevelVerticesNumberMap());
            else
                writer.println(prefix + "└──── IndexTreeNode: " + node.getNodeValueMap());
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

    public void close() {
        writer.close();
    }


}
