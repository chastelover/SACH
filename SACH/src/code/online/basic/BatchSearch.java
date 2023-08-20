package code.online.basic;

import code.online.MetaPath;

import java.util.*;

public class BatchSearch {
	private int graph[][] = null;//data graph, including vertex IDs, edge IDs, and their link relationships
	private int vertexType[] = null;//vertex -> type
	private int edgeType[] = null;//edge -> type
	private MetaPath queryMPath = null;
	
	public BatchSearch(int graph[][], int vertexType[], int edgeType[], MetaPath queryMPath) {
		this.graph = graph;
		this.vertexType = vertexType;
		this.edgeType = edgeType;
		this.queryMPath = queryMPath;
	}
	
/*	public Set<Integer> collect(Set<Integer> startSet, Set<Integer> keepSet) {
		int pathLen = queryMPath.pathLen;
		Set<Integer> batchSet = startSet;
		for(int index = 0;index < pathLen;index ++) {
			int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];
			Set<Integer> nextBatchSet = new HashSet<Integer>();
			for(int anchorId:batchSet) {
				int nbArr[] = graph[anchorId];
				for(int i = 0;i < nbArr.length;i += 2) {
					int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						if(index == queryMPath.pathLen - 1) {//impose restriction
							if(keepSet.contains(nbVertexID)) {
								nextBatchSet.add(nbVertexID);
							}
						}else {
							nextBatchSet.add(nbVertexID);
						}
					}
				}
			}
			batchSet = nextBatchSet;
		}
		return batchSet;
	}

	public Map<Integer, Set<Integer>> collectAndBuildGraph(Set<Integer> startSet, Set<Integer> keepSet) {
		int pathLen = queryMPath.pathLen;
		int halfPathLen = pathLen / 2;
		Set<Integer> batchSet = startSet;
		Set<Integer> cacheSet = new HashSet<>();
		// 从长元路径中全部顶点开始扩展一半的元路径
		for(int index = 0;index < halfPathLen;index ++) {
			int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];
			for(int anchorId:batchSet) {
				int nbArr[] = graph[anchorId];
				for(int i = 0;i < nbArr.length;i += 2) {
					int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						cacheSet.add(nbVertexID);
					}
				}
			}
		}
		System.out.println("短元路径基于长元路径扩同质图顶点扩展出的M数量: "+ cacheSet.size());
		// 处理每个中间顶点
		Map<Integer, Set<Integer>> subPnbMap = new HashMap<>();
		for(int index = halfPathLen;index < pathLen;index ++) {
			int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];
			for(int anchorId:cacheSet) {
				int nbArr[] = graph[anchorId];
				Set<Integer> temp = new HashSet<>();
				for(int i = 0;i < nbArr.length;i += 2) {
					int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						temp.add(nbVertexID);
					}
				}
				Set<Integer> intersectionSet = new HashSet<>();
				intersectionSet.addAll(temp);
				intersectionSet.retainAll(keepSet);
				for (int v: intersectionSet) {
					Set<Integer> nb = new HashSet<>();
					if (subPnbMap.containsKey(v)) {
						nb = subPnbMap.get(v);
					}
					for (int n: intersectionSet){
						if (v != n) {
							nb.add(n);
						}
					}
					subPnbMap.put(v,nb);
				}
			}
		}

		return subPnbMap;
	}

	public Map<Integer, Set<Integer>> collectAndBuildGraph2(Set<Integer> startSet, Set<Integer> keepSet) {
		int pathLen = queryMPath.pathLen;
		int halfPathLen = pathLen / 2;
		Set<Integer> batchSet = startSet;
		Set<Integer> cacheSet = new HashSet<>();
		// 从长元路径中全部顶点开始扩展一半的短元路径
		for(int index = 0;index < pathLen / 2;index ++) {
			int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];
			Set<Integer> nextBatchSet = new HashSet<Integer>();
			for(int anchorId:batchSet) {
				int nbArr[] = graph[anchorId];
				for(int i = 0;i < nbArr.length;i += 2) {
					int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						if(index == (queryMPath.pathLen / 2) - 1) {//impose restriction
							if(keepSet.contains(nbVertexID)) {
								nextBatchSet.add(nbVertexID);
							}
							cacheSet.add(nbVertexID);
						}else {
							nextBatchSet.add(nbVertexID);
						}
					}
				}
			}
			batchSet = nextBatchSet;
		}
		System.out.println("短元路径基于长元路径扩同质图顶点扩展出的中间类型数量: "+ cacheSet.size());
		// 处理每个中间顶点
		Map<Integer, Set<Integer>> subPnbMap = new HashMap<>();
		for(int index = halfPathLen;index < pathLen;index ++) {
			int targetVType = queryMPath.vertex[index + 1], targetEType = queryMPath.edge[index];
			Set<Integer> nextBatchSet = new HashSet<Integer>();
			for(int anchorId:cacheSet) {
				int nbArr[] = graph[anchorId];
				Set<Integer> temp = new HashSet<>();
				for(int i = 0;i < nbArr.length;i += 2) {
					int nbVertexID = nbArr[i], nbEdgeID = nbArr[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						if(index == queryMPath.pathLen - 1) {//impose restriction
							if(keepSet.contains(nbVertexID)) {
								nextBatchSet.add(nbVertexID);
							}
							temp.add(nbVertexID);
						}else {
							nextBatchSet.add(nbVertexID);
						}
					}
				}
				if (index == queryMPath.pathLen - 1) {
//					System.out.println("ok");
					Set<Integer> intersectionSet = new HashSet<>();
					intersectionSet.addAll(temp);
					intersectionSet.retainAll(keepSet);
					for (int v: intersectionSet) {
						Set<Integer> nb = new HashSet<>();
						if (subPnbMap.containsKey(v)) {
							nb = subPnbMap.get(v);
						}
						for (int n: intersectionSet){
							if (v != n) {
								nb.add(n);
							}
						}
						subPnbMap.put(v,nb);
					}
				}

			}
			cacheSet = nextBatchSet;
		}

		return subPnbMap;
	}*/
	
	public Set<Integer> collect(int startId, Set<Integer> keepSet) {
		Set<Integer> anchorSet = new HashSet<Integer>();
		anchorSet.add(startId);

		for(int layer = 0;layer < queryMPath.pathLen;layer ++) {
			int targetVType = queryMPath.vertex[layer + 1], targetEType = queryMPath.edge[layer];
			
			Set<Integer> nextAnchorSet = new HashSet<Integer>();
			for(int anchorId:anchorSet) {
				int nb[] = graph[anchorId];
				for(int i = 0;i < nb.length;i += 2) {
					int nbVertexID = nb[i], nbEdgeID = nb[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						if(layer < queryMPath.pathLen - 1) {
							nextAnchorSet.add(nbVertexID);
						}else {
							if(keepSet.contains(nbVertexID))   nextAnchorSet.add(nbVertexID);//impose restriction FIXME
						}
					}
				}
			}
			anchorSet = nextAnchorSet;
		}
		
		anchorSet.remove(startId);//2018-9-19-bug: remove the duplicated vertex
		return anchorSet;
	}

	//public Set<Integer> collectWithCache(int startId, Set<Integer> keepSet, Set<Integer> visited) {
	public List<Set> collectWithCache(int startId, Set<Integer> keepSet, Set<Integer> visited) {
		Set<Integer> anchorSet = new HashSet<Integer>();
		anchorSet.add(startId);
		Set<Integer> nbInVisitedSet = new HashSet<>();

		for(int layer = 0;layer < queryMPath.pathLen;layer ++) {
			int targetVType = queryMPath.vertex[layer + 1], targetEType = queryMPath.edge[layer];

			Set<Integer> nextAnchorSet = new HashSet<Integer>();
			for(int anchorId:anchorSet) {

				int nb[] = graph[anchorId];
				for(int i = 0;i < nb.length;i += 2) {
					int nbVertexID = nb[i], nbEdgeID = nb[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						if(layer < queryMPath.pathLen - 1) {
							nextAnchorSet.add(nbVertexID);
						}else {
							if(keepSet.contains(nbVertexID) ) {
								if (!visited.contains(nbVertexID)) {
									nextAnchorSet.add(nbVertexID);//impose restriction FIXME
								}
								else nbInVisitedSet.add(nbVertexID);
							}
						}
					}
				}
			}
			anchorSet = nextAnchorSet;
		}
//		anchorSet.addAll(nbInVisitedSet);//已经访问过的节点不再遍历，但加入到邻居中？
		anchorSet.remove(startId);//2018-9-19-bug: remove the duplicated vertex
		HashSet<Integer> realAnchorSet = new HashSet<>(anchorSet);
		realAnchorSet.addAll(nbInVisitedSet);
		List<Set> resultList = new ArrayList<>();
		resultList.add(anchorSet);
		resultList.add(realAnchorSet);
		return resultList;
	}

	public Queue<Integer> collect0511(int startId, Set<Integer> keepSet) {
		Set<Integer> anchorSet = new HashSet<Integer>();
		anchorSet.add(startId);

		for(int layer = 0;layer < queryMPath.pathLen;layer ++) {
			int targetVType = queryMPath.vertex[layer + 1], targetEType = queryMPath.edge[layer];

			Set<Integer> nextAnchorSet = new HashSet<Integer>();
			for(int anchorId:anchorSet) {
				int nb[] = graph[anchorId];
				for(int i = 0;i < nb.length;i += 2) {
					int nbVertexID = nb[i], nbEdgeID = nb[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						if(layer < queryMPath.pathLen - 1) {
							nextAnchorSet.add(nbVertexID);
						}else {
							if(keepSet.contains(nbVertexID))   nextAnchorSet.add(nbVertexID);//impose restriction FIXME
						}
					}
				}
			}
			anchorSet = nextAnchorSet;
		}

		anchorSet.remove(startId);//2018-9-19-bug: remove the duplicated vertex
		Queue<Integer> anchorQueue = new LinkedList<Integer>(anchorSet);
		return anchorQueue;
	}

	public Set<Integer> collect0509(int startId, Set<Integer> keepSet) {
		Set<Integer> anchorSet = new HashSet<Integer>();
		anchorSet.add(startId);

		for(int layer = 0;layer < queryMPath.pathLen;layer ++) {
			int targetVType = queryMPath.vertex[layer + 1], targetEType = queryMPath.edge[layer];

			Set<Integer> nextAnchorSet = new HashSet<Integer>();
			for(int anchorId:anchorSet) {
				int nb[] = graph[anchorId];
				for(int i = 0;i < nb.length;i += 2) {
					int nbVertexID = nb[i], nbEdgeID = nb[i + 1];
					if(targetVType == vertexType[nbVertexID] && targetEType == edgeType[nbEdgeID]) {
						if(layer < queryMPath.pathLen - 1) {
							nextAnchorSet.add(nbVertexID);
						}else {
							if(keepSet.contains(nbVertexID))   nextAnchorSet.add(nbVertexID);//impose restriction FIXME
						}
					}
				}
			}
			anchorSet = nextAnchorSet;
		}

		anchorSet.remove(startId);//2018-9-19-bug: remove the duplicated vertex
		return anchorSet;
	}
}
