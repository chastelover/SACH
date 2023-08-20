package code.index.basic;

import java.util.*;

public class GraphComponent {
    private Set<Integer> vertices = new HashSet<>();
    private int k = 0;

    public GraphComponent(int[] vertex){
        for (int j : vertex) {
            this.vertices.add(j);
        }
    }

    public GraphComponent(int[] vertex, int k){
        for (int j : vertex) {
            this.vertices.add(j);
        }
        this.k = k;
    }

    public int getK(){
        return this.k;
    }

    public void setK(int k){
        this.k = k;
    }

    public boolean isEmpty(){
        return vertices.isEmpty();
    }

    public int size(){
        return vertices.size();
    }

    public boolean contains(int v){
        return vertices.contains(v);
    }

    public List<Integer> getVerticesWithMinAttribute(int[] component, ConstructIACHIndex constructIACHIndex){
        double minValue = Integer.MAX_VALUE;
        HashMap<Integer, Double> attributeMap = new HashMap<>();
        for (int vertex: component){
            attributeMap.put(vertex, constructIACHIndex.getWeight()[vertex]);
        }
        List<Integer> minKeys = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : attributeMap.entrySet()) {
            double value = entry.getValue();
            if (value < minValue) {
                minValue = value;
                minKeys.clear();
                minKeys.add(entry.getKey());
            }
            else if (value == minValue) {
                minKeys.add(entry.getKey());
            }
        }
        return minKeys;
    }

    public void removeVertexAndEdges(int v){
        vertices.remove(v);

    }


}
