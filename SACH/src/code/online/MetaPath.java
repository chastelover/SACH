package code.online;

import code.util.Dictionary;

import javax.swing.text.SimpleAttributeSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author fangyixiang
 * @date 10 Sep. 2018
 * 
 * A meta-path with (pathLen + 1) vertices and pathLen edges
 */
public class MetaPath {
	public int[] vertex;
	public int[] edge;
	public int pathLen = -1;
	public String[] vertexName;
	public String[] edgeName;

	public MetaPath(int[] vertex, int[] edge) {
		this.vertex = vertex;
		this.edge = edge;
		this.pathLen = edge.length;//the number of relations in a meta-path


		if(vertex.length != edge.length + 1) {
			System.out.println("the meta-path is incorrect");
		}
	}

	/**
	 * @Description : 将节点名称转成int数组，用那个数据集在Dictionary实例化的时候确定
	 * @Date : 2023-06-16
	 */

	public MetaPath(String[] vertexName, Dictionary dictionary){
		int[] vertexId = calVertexId(vertexName, dictionary);  // 得到将元路径节点字母转成int数组

		String[] edge = calEdge(vertexName);
		int[] edge2Id = calEdge2ID(edge, dictionary);  // 得到将元路径路径字母转成int数组
		this.vertex = vertexId;
		this.edge = edge2Id;
		this.pathLen = edge2Id.length;
		this.vertexName = vertexName;
		this.edgeName = edge;

		if(vertex.length != edge.length + 1) {
			System.out.println("the meta-path is incorrect");
		}
	}

	public MetaPath(String metaPathStr) {
		String s[] = metaPathStr.trim().split(" ");
		this.pathLen = s.length / 2;
		this.vertex = new int[pathLen +1];
		this.edge = new int[pathLen];

		for(int i = 0;i < s.length;i ++) {
			int value = Integer.parseInt(s[i]);
			if(i % 2 == 0) {
				vertex[i / 2] = value;
			}else {
				edge[i / 2] = value;
			}
		}
	}

	public String toString() {
		String str = "";
		for(int i = 0;i < pathLen;i ++) {
			str += vertex[i] + "-" + edge[i] + "-";
		}
		str += vertex[pathLen];
		return str;
	}

	public void convertId2Name(Map<Integer, String> vertexMap, Map<Integer, String> edgeMap) {
		List<String>vNameList = new ArrayList<>();
		List<String>eNameList = new ArrayList<>();
		for (int j : this.vertex) {
			vNameList.add(vertexMap.get(j));
		}
		for (int i : this.edge){
			eNameList.add(edgeMap.get(i));
		}
		this.vertexName = vNameList.toArray(new String[vNameList.size()]);
		this.edgeName = eNameList.toArray(new String[eNameList.size()]);
	}

	public String printVertexName(){
		return Arrays.toString(this.vertexName);
	}

	public int[] calVertexId(String[] vertex, Dictionary dictionary){
		int[] vertexId = new int[vertex.length];

		for (int i = 0; i < vertex.length; i++) {
			if (dictionary.vertex2ID.containsKey(vertex[i])){
				vertexId[i] = dictionary.vertex2ID.get(vertex[i]);
			}
		}
		return vertexId;
	}

	public String[] calEdge(String[] vertex){
		int newArrayLength = vertex.length - 1; // 新数组的长度
		String[] newArray = new String[newArrayLength]; // 创建新数组

		// 遍历原数组并将元素连接存储到新数组中
		for (int i = 0; i < newArrayLength; i++) {
			newArray[i] = vertex[i] + "->" + vertex[i+1];
		}
		return newArray;
	}

	public int[] calEdge2ID(String[] edge, Dictionary dictionary){
		int[] edgeId = new int[edge.length];

		for (int i = 0; i < edge.length; i++) {
			if (dictionary.edge2ID.containsKey(edge[i])){
				edgeId[i] = dictionary.edge2ID.get(edge[i]);
			}
		}
		return edgeId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		MetaPath metaPath = (MetaPath) obj;
		if (this.pathLen != metaPath.pathLen) {
            return false;
        }
		if (this.vertex.length != metaPath.vertex.length){
			return false;
		}
		if (this.edge.length != metaPath.edge.length){
			return false;
		}
		for (int i = 0; i < this.vertex.length; i++){
			if (this.vertex[i] != metaPath.vertex[i]){
                return false;
            }
		}

		for (int i = 0; i < this.edge.length; i++){
			if (this.edge[i] != metaPath.edge[i]){
				return false;
			}
		}
		return true;
	}
}
