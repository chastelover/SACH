package code.util;

import java.util.HashMap;
import java.util.Map;


public class CustomHashMap<K, V> extends HashMap<K, V> {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<");

        for (Map.Entry<K, V> entry : entrySet()) {
            K outerKey = entry.getKey();
            V innerMap = entry.getValue();
            sb.append(outerKey).append(" ↠ [");

            if (innerMap instanceof Map) {
                for (Map.Entry<?, ?> innerEntry : ((Map<?, ?>) innerMap).entrySet()) {
                    sb.append(innerEntry.getKey()).append(" → ").append(innerEntry.getValue()).append(", ");
                }
                // 删除最后多余的逗号和空格
                if (!((Map<?, ?>) innerMap).isEmpty()) {
                    sb.delete(sb.length() - 2, sb.length());
                }
            }

            sb.append("], ");
        }
        // 删除最后多余的逗号和空格
        if (!isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }

        sb.append(">");
        return sb.toString();
    }

    public static void main(String[] args) {
        // 创建Map<Integer, Map<Integer, Integer>>对象
        CustomHashMap<Integer, Map<Integer, Integer>> map = new CustomHashMap<>();
        Map<Integer, Integer> innerMap1 = new HashMap<>();
        innerMap1.put(1, 100);
        innerMap1.put(2, 200);

        Map<Integer, Integer> innerMap2 = new HashMap<>();
        innerMap2.put(3, 300);
        innerMap2.put(4, 400);

        map.put(10, innerMap1);
        map.put(20, innerMap2);

        // 打印Map
        System.out.println(map);
    }
}