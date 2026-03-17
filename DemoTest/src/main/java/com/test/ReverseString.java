package com.test;

/**
 * 字符串反转工具类
 * 提供不依赖 API 的手写字符串反转方法
 *
 * @author vnstyz
 * @version v0.1
 */
public class ReverseString {

    /**
     * 反转字符串（双指针法）
     * 将字符串转成字符数组，然后用两个指针从两边往中间交换位置
     * 最后再转回字符串返回
     *
     * @param str 需要反转的字符串
     * @return 反转后的字符串
     */
    public static String reverse(String str) {
        // 如果字符串是 null 或者空串，直接返回
        if (str == null || str.length() == 0) {
            return str;
        }

        // 转成字符数组方便操作
        char[] chars = str.toCharArray();
        int left = 0;              // 左指针，从左边开始
        int right = chars.length - 1;  // 右指针，从右边开始

        // 两个指针往中间移动，交换对应位置的字符
        while (left < right) {
            // 交换左右两边的字符
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;

            // 左指针往右移，右指针往左移
            left++;
            right--;
        }

        // 把字符数组转回字符串
        return new String(chars);
    }

    /**
     * 反转字符串（递归方式）
     * 用递归实现，每次把第一个字符放到最后面，然后递归处理剩下的部分
     *
     * @param str 需要反转的字符串
     * @return 反转后的字符串
     */
    public static String reverseRecursive(String str) {
        // 如果字符串是 null 或者长度小于等于 1，直接返回
        if (str == null || str.length() <= 1) {
            return str;
        }

        // 递归处理：把第一个字符移到最后，剩下的继续递归
        return reverseRecursive(str.substring(1)) + str.charAt(0);
    }

    /**
     * 反转字符串（StringBuilder 方式）
     * 从后往前遍历字符串，把每个字符追加到 StringBuilder 里
     * 注意：这里没有用 StringBuilder 的 reverse() 方法
     *
     * @param str 需要反转的字符串
     * @return 反转后的字符串
     */
    public static String reverseWithBuilder(String str) {
        // 如果字符串是 null 或者空串，直接返回
        if (str == null || str.length() == 0) {
            return str;
        }

        // 创建 StringBuilder 用来拼接字符
        StringBuilder result = new StringBuilder(str.length());

        // 从最后一个字符开始往前遍历
        for (int i = str.length() - 1; i >= 0; i--) {
            result.append(str.charAt(i));
        }

        return result.toString();
    }
}
