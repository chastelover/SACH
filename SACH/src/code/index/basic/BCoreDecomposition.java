package code.index.basic;

import code.index.Decomposition;
import code.index.HomoGraphBuilder;
import code.index.KCore;
import code.online.MetaPath;
import code.util.DataReader;

import java.util.*;

import static code.index.KConnectedComponents.getKConnectedComponents;
import static code.index.TestIACHIndex.getConfig;

/**
 * @author fangyixiang
 * @date Oct 15, 2018
 * Perform basic k-core decomposition
 */
public class BCoreDecomposition implements Decomposition {
	private int graph[][] = null;//data graph, including vertex IDs, edge IDs, and their link relationships
	private int vertexType[] = null;//vertex -> type
	private int edgeType[] = null;//edge -> type
	private int maxK = 0;

	public BCoreDecomposition(int graph[][], int vertexType[], int edgeType[]) {
		this.graph = graph;
		this.vertexType = vertexType;
		this.edgeType = edgeType;

	}

	private int reverseOrderArr[] = null;

	public Map<Integer, Integer> decompose(MetaPath queryMPath){
		//step 0: build a homogeneous graph
		HomoGraphBuilder b2 = new HomoGraphBuilder(graph, vertexType, edgeType, queryMPath);
		Map<Integer, int[]> pnbMap = b2.build();

		int newID = 1;
		Map<Integer, Integer> oldToNewMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> newToOldMap = new HashMap<Integer, Integer>();
		for(int id:pnbMap.keySet()) {
			oldToNewMap.put(id, newID);//oldID -> newID
			newToOldMap.put(newID, id);//newID -> oldID
			newID ++;
		}

		//step 1: build a sub-graph
		int subGraph[][] = new int[pnbMap.size() + 1][];
		for(int id:pnbMap.keySet()) {
			int pnbArr[] = pnbMap.get(id);

			int curID = oldToNewMap.get(id);
			subGraph[curID] = new int[pnbArr.length];
			for(int j = 0;j < pnbArr.length;j ++) {
				int nbID = oldToNewMap.get(pnbArr[j]);
				subGraph[curID][j] = nbID;
			}
		}
		pnbMap = null;

//		//test codes
//		double memory = subGraph.length;
//		for(int i = 1;i < subGraph.length;i ++){
//			memory += subGraph[i].length;
//		}
//		if(memory > 20000000){
//			System.out.println("------------------------------------------> " + queryMPath.toString() + " " + memory);
//		}

		//step 2: kcore decomposition
		KCore kc = new KCore(subGraph);
		int subCore[] = kc.decompose();
		reverseOrderArr = kc.obtainReverseCoreArr();
		for(int i = 0;i < reverseOrderArr.length;i ++) {
			int tmpNewID = reverseOrderArr[i];
			int tmpOldID = newToOldMap.get(tmpNewID);
			reverseOrderArr[i] = tmpOldID;
		}

		//step 3: attach the core number
		Map<Integer, Integer> vertexCoreMap = new HashMap<Integer, Integer>();
		for(int i = 1;i < subCore.length;i ++) {
			int oldId = newToOldMap.get(i);
			int core = subCore[i];
			vertexCoreMap.put(oldId, core);
		}

		return vertexCoreMap;
	}

	public Map<Integer, int[]> decomposeAndGetGraph(MetaPath queryMPath){
		//step 0: build a homogeneous graph
		HomoGraphBuilder b2 = new HomoGraphBuilder(graph, vertexType, edgeType, queryMPath);
		Map<Integer, int[]> pnbMap = b2.build();

		int newID = 1;
		Map<Integer, Integer> oldToNewMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> newToOldMap = new HashMap<Integer, Integer>();
		for(int id:pnbMap.keySet()) {
			oldToNewMap.put(id, newID);//oldID -> newID
			newToOldMap.put(newID, id);//newID -> oldID
			newID ++;
		}

		//step 1: build a sub-graph
		int subGraph[][] = new int[pnbMap.size() + 1][];
		for(int id:pnbMap.keySet()) {
			int pnbArr[] = pnbMap.get(id);

			int curID = oldToNewMap.get(id);
			subGraph[curID] = new int[pnbArr.length];
			for(int j = 0;j < pnbArr.length;j ++) {
				int nbID = oldToNewMap.get(pnbArr[j]);
				subGraph[curID][j] = nbID;
			}
		}

		//step 2: kcore decomposition
		KCore kc = new KCore(subGraph);
		int subCore[] = kc.decompose();
		reverseOrderArr = kc.obtainReverseCoreArr();
		for(int i = 0;i < reverseOrderArr.length;i ++) {
			int tmpNewID = reverseOrderArr[i];
			int tmpOldID = newToOldMap.get(tmpNewID);
			reverseOrderArr[i] = tmpOldID;
		}

		//step 3: attach the core number
		Map<Integer, Integer> vertexCoreMap = new HashMap<Integer, Integer>();
		for(int i = 1;i < subCore.length;i ++) {
			int oldId = newToOldMap.get(i);
			int core = subCore[i];
			vertexCoreMap.put(oldId, core);
		}

		this.maxK = vertexCoreMap.get(reverseOrderArr[0]);

		return pnbMap;
	}

	public int[] getReverseOrderArr() {
		return reverseOrderArr;
	}

	public int getMaxK() {
		return maxK;
	}

	public static void main(String[] args) {
		int[]vertex2 = {1, 0, 1};  // AMA
        int[]edge2 = {1, 0};  // A->M, M->A
		MetaPath mp2 = new MetaPath(vertex2, edge2);

		int[]vertex1 = {1, 0, 2, 0, 1};  // AMDMA
		int[]edge1 = {1, 2, 3, 0};
		MetaPath mp1 = new MetaPath(vertex1, edge1);

		int[]vertex = {1, 0, 3, 0, 1}; // AMWMA
		int[]edge = {1, 4, 5, 0}; //A->M, M->W, W->M, M->A
		MetaPath mp = new MetaPath(vertex, edge);




		DataReader dataReader = getConfig("imdb");
		int[][] graph = dataReader.readGraph();
		int[] vertexType = dataReader.readVertexType();
		int[] edgeType = dataReader.readEdgeType();

		int[] queryKList = new int[]{4,8,16,32,64};
//		int[] queryQList = new int[]{139551, 111495, 40002, 40001, 62081, 34306, 40003, 75373, 34307, 111496, 38413, 91603, 31888, 22387, 91604, 80624, 75374, 43702, 120949, 102812, 63292, 145502, 63293, 342131};
		int[] queryQList = new int[]{102638, 302980, 698121, 597386, 432002, 865672, 796809, 278658, 781059, 234889};
		BCoreDecomposition b = new BCoreDecomposition(graph, vertexType, edgeType);
//		Map<Integer, int[]> pnbMap = b.decomposeAndGetGraph(mp);
//		List<Map<Integer, int[]>> kConnectedComponents = getKConnectedComponents(pnbMap, k);
//		System.out.println(b.getMaxK());
//
		long t1 = System.nanoTime();
		Map<Integer, int[]> pnbMap = b.decomposeAndGetGraph(mp2);
//		k = b.maxK;
		for (int queryK : queryKList) {
			List<Map<Integer, int[]>> kConnectedComponents = getKConnectedComponents(pnbMap, queryK);
			long t2 = System.nanoTime();
			float time = (float) (t2 - t1) / 1000000000;
			System.out.println("k = " + queryK + "时时间为" + time + "s!");
			for (int i = 0; i < kConnectedComponents.size(); i++) {
//				for (int queryID:queryQList){
				int queryID = 139551;
				Map<Integer, int[]> connectedComponent = kConnectedComponents.get(i);
//				System.out.println("Connected Component " + (i + 1) + ":");
				for (int key : connectedComponent.keySet()) {
//					System.out.println("k = " + key + ", vertices = " + Arrays.toString(connectedComponent.get(key)));
					Set<Integer>set = new HashSet<>();
					for (int item: connectedComponent.get(key)){
						set.add(item);
					}
					if (set.contains((queryID))) {
						System.out.println("社区大小为：" + Arrays.toString(connectedComponent.get(key)).length());
					}
				}
//				}
			}
		}
//		System.out.println(kConnectedComponents);

//		Map<Integer, int[]> pnbMap = b.decomposeAndGetGraph(mp2);
//		List<Map<Integer, int[]>> kConnectedComponents = getKConnectedComponents(pnbMap, k);
		System.out.println(b.getMaxK());
	}
}
