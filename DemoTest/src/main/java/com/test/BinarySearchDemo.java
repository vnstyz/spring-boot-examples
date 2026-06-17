package com.test;

/**
 * 二分查找算法示例
 * 要求输入数组必须已排序
 */
public class BinarySearchDemo {
    /**
     * 执行二分查找
     * @param arr 已排序的整数数组
     * @param target 要查找的目标值
     * @return 目标值的索引，若未找到返回-1
     */
    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            // 防止整数溢出
            int mid = left + (right - left) / 2;

            // 找到目标值
            if (arr[mid] == target) {
                return mid;
            }

            // 在右半部分查找
            if (arr[mid] < target) {
                left = mid + 1;
            }
            // 在左半部分查找
            else {
                right = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        // 测试数组（必须已排序）
        int[] sortedArray = {2, 3, 4, 10, 40};
        int target = 10;

        int result = binarySearch(sortedArray, target);

        if (result != -1) {
            System.out.println("目标值 " + target + " 在索引位置: " + result);
        } else {
            System.out.println("数组中未找到目标值");
        }

        // 验证边界情况
        System.out.println("\n边界测试:");
        System.out.println("查找 2: " + binarySearch(sortedArray, 2));  // 应该返回 0
        System.out.println("查找 40: " + binarySearch(sortedArray, 40)); // 应该返回 4
        System.out.println("查找 5: " + binarySearch(sortedArray, 5));  // 应该返回 -1
    }
}