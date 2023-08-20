package code.index.basic;

/* 暂时废弃，使用新的
public class IndexTree {
    private IndexKNode root;

    public IndexTree(int node){
        this.root = new IndexKNode(node);
    }

    public IndexTree(int node, double value){
        this.root = new IndexTreeNode(node, value);
    }

    public IndexTree(int node, ConstructIACHIndex constructIACHIndex){
        this.root = new IndexTreeNode(node, constructIACHIndex);
    }

    public IndexTree(int[] nodes){
        this.root = new IndexTreeNode(nodes);
    }

    public IndexTree(int[] nodes, double[] values, boolean isOriginalWeights){
        this.root = new IndexTreeNode(nodes, values, isOriginalWeights);
    }

    public void implementRootValue(ConstructIACHIndex constructIACHIndex){
        this.root.implementValue(constructIACHIndex);
    }

    public IndexTreeNode getRoot() {
        return root;
    }

    public void setRoot(IndexTreeNode root){
        this.root = root;
    }

    public void addIndexTreeChildren(IndexTreeNode node, List<IndexTreeNode>children){
        node.addChildren(children);
    }

    public void addIndexTreeChildren(int node, List<IndexTreeNode>children){
        IndexTreeNode indexTreeNode = new IndexTreeNode(node);
        indexTreeNode.addChildren(children);
    }

    public void addIndexTreeChildren(int node, double value, List<IndexTreeNode>children){
        IndexTreeNode indexTreeNode = new IndexTreeNode(node, value);
        indexTreeNode.addChildren(children);
    }

    public void addIndexTreeChildren(int node, int[] nodes, boolean allNodesOneLeaf){
        IndexTreeNode indexTreeNode = new IndexTreeNode(node);
        List<IndexTreeNode>children = new ArrayList<>();
        if (!allNodesOneLeaf) {
            for (int v : nodes) {
                IndexTreeNode child = new IndexTreeNode(v);
                children.add(child);
            }
            indexTreeNode.addChildren(children);
        }
        else {
            IndexTreeNode child = new IndexTreeNode(nodes);
            indexTreeNode.addChild(child);
        }

    }

    public void insertRightNode(IndexTreeNode indexTreeNode, int node){
        IndexTreeNode rightRode = new IndexTreeNode(node);
        indexTreeNode.insertRight(rightRode);
    }

    public void insertRightNode(IndexTreeNode indexTreeNode, int[] nodes){
        IndexTreeNode node = new IndexTreeNode(nodes);
        indexTreeNode.insertRight(node);
    }

    private static void printTree(IndexTreeNode node, int level) {
        StringBuilder indent = new StringBuilder();
        indent.append("  ".repeat(Math.max(0, level)));
        System.out.println(indent.toString() + node.getValue());

        List<IndexTreeNode> children = node.getChildren();
        for (IndexTreeNode child : children) {
            printTree(child, level + 1);
        }
    }
}
*/