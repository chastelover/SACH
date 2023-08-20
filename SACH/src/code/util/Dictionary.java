package code.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary {
    public Map<String, Integer>datasetName2ID = new HashMap<>();
    public Map<Integer, String>id2Vertex = new HashMap<>();
    public Map<Integer, String>id2Edge = new HashMap<>();
    public Map<String, Integer>vertex2ID = new HashMap<>();
    public Map<String, Integer>edge2ID = new HashMap<>();

    public void initDataName(){
        datasetName2ID.put("dblp", 1);
        datasetName2ID.put("foursquare", 2);
        datasetName2ID.put("imdb", 3);
        datasetName2ID.put("PubMed", 4);
    }

    public Dictionary(){}

    public void loadMappingsFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || !line.contains("<")) {
                    continue; // 跳过空行和注释行
                }

                int rightIndex = line.lastIndexOf('>');
                String str = line.substring(1, rightIndex);
                if (str.contains("->")){
                    List<Character>temp = new ArrayList<>();
                    for (int i = 0; i < str.length(); i++) {
                        char c = str.charAt(i);
                        if (Character.isUpperCase(c)){
                            temp.add(c);
                        }
                    }
                    String edgeName = temp.get(0) + "->" + temp.get(1);
                    int edgeID = 0;
                    try {
                        edgeID = Integer.parseInt(line.substring(rightIndex + 2, line.length() - 1));
                    }
                    catch (Exception e){
                        edgeID = Integer.parseInt(line.substring(rightIndex + 4, line.length() - 1));
                    }

                    id2Edge.put(edgeID, edgeName);
                }
                else {
                    String vertexName = str.substring(0, 1);
                    int vertexID = Integer.parseInt(line.substring(rightIndex + 4, line.length() - 1));
                    id2Vertex.put(vertexID, vertexName);
                }

            }
        }
    }

    public void completeMap(){
        for (Map.Entry<Integer, String>entry: id2Vertex.entrySet()){
            vertex2ID.put(entry.getValue(), entry.getKey());
        }
        for (Map.Entry<Integer, String>entry: id2Edge.entrySet()){
            edge2ID.put(entry.getValue(), entry.getKey());
        }
    }

    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
        dictionary.initDataName();
        try {
            dictionary.loadMappingsFromFile(Config.dblpReadme);
            dictionary.completeMap();
            System.out.println(dictionary.id2Vertex);
            System.out.println(dictionary.id2Edge);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
