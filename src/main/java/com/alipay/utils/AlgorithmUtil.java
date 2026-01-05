//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alipay.utils;

import static com.alipay.utils.DoubleUtil.round;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AlgorithmUtil {
  public static double[] maxSlidingWindow(double[] nums, int size) {
    if (nums != null && nums.length > 0 && size > 0) {
      LinkedList<Integer> queue = Lists.newLinkedList();
      double[] result = new double[nums.length - size + 1];

      for (int i = 0; i < nums.length; ++i) {
        while (!queue.isEmpty() && nums[(Integer) queue.peekLast()] <= nums[i]) {
          queue.pollLast();
        }

        queue.addLast(i);
        if (!queue.isEmpty() && (Integer) queue.peek() <= i - size) {
          queue.poll();
        }

        if (i + 1 >= size && !queue.isEmpty()) {
          result[i + 1 - size] = nums[(Integer) queue.peek()];
        }
      }

      return result;
    } else {
      return new double[0];
    }
  }

  public static double[] averageSlidingWindow(double[] nums, int size) {
    if (nums != null && nums.length > 0 && size > 0) {
      double[] result = new double[nums.length - size + 1];
      Queue<Double> qu = new LinkedList();
      int ind = 0;

      for (double num : nums) {
        if (qu.size() == size) {
          result[ind++] = qu.stream().mapToDouble((value) -> value).average().getAsDouble();
          result[ind] = round(result[ind]);
          qu.poll();
          qu.offer(num);
        } else {
          qu.offer(num);
        }
      }

      result[ind] = qu.stream().mapToDouble((value) -> value).average().getAsDouble();
      result[ind] = round(result[ind]);
      return result;
    } else {
      return new double[0];
    }
  }

  public static List<Double> averageByChunk(List<Double> list, int chunkSize) {
    if (list == null || list.isEmpty()) {
      return new ArrayList<>();
    }

    int size = list.size();
    int chunks = (int) Math.ceil((double) size / chunkSize);
    List<Double> result = new ArrayList<>(chunks);

    for (int i = 0; i < chunks; i++) {
      int start = i * chunkSize;
      int end = Math.min(start + chunkSize, size);

      double average =
          list.subList(start, end).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
      average = round(average);

      result.add(average);
    }

    return result;
  }
}
