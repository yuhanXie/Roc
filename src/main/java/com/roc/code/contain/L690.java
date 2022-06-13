package com.roc.code.contain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author gang.xie
 */
public class L690 {


    /**
     * L699
     *
     * @param positions positions
     * @return
     */
    public List<Integer> fallingSquares(int[][] positions) {
        //treeMap是有序的map， [key1, value1] [key2,value2]表示从key1到key2区间的最高点为value1，以此递推
        int n = positions.length;
        List<Integer> result = new ArrayList<>();
        TreeMap<Integer, Integer> heightMap = new TreeMap<>();
        heightMap.put(0, 0);
        for (int i = 0; i < n; i++) {
            int left = positions[i][0];
            int length = positions[i][1];
            int right = left + length - 1;
            //找到大于key的最小值，如果没有的话，就返回null
            Integer higherLeft = heightMap.higherKey(left);
            Integer higherRight = heightMap.higherKey(right);

            Integer preLeft = higherLeft != null ? heightMap.lowerKey(higherLeft) : heightMap.lastKey();
            Integer preRight = higherRight == null ?
                    heightMap.lastKey() : heightMap.lowerKey(higherRight);
            Integer rightHeight = preRight == null ? 0 : heightMap.get(preRight);

            int height = 0;
            //找到和前面重叠的区域，并取得最大值
            Map<Integer, Integer> tail = preLeft != null ? heightMap.tailMap(preLeft) : heightMap;
            for (Map.Entry<Integer, Integer> entry : tail.entrySet()) {
                if (entry.getKey() == higherRight) {
                    break;
                }
                height = Math.max(height, entry.getValue() + length);
            }

            Set<Integer> keySet = new TreeSet<>(tail.keySet());
            for (Integer key : keySet) {
                if (higherLeft == null || key < higherLeft) {
                    continue;
                }
                if (higherRight != null && key >= higherRight) {
                    break;
                }
                heightMap.remove(key);
            }
            heightMap.put(left, height);
            if (higherRight == null || higherRight != right + 1) {
                heightMap.put(right + 1, rightHeight);
            }
            result.add(i > 0 ? Math.max(result.get(i - 1), height) : height);
        }
        return result;
    }
}
