package org.example.hutooldemo;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 随机休眠演示：使用 Hutool 工具库实现 0~60 秒随机时间休眠。
 *
 * 用到的 Hutool 工具类：
 * - RandomUtil  — 生成随机数
 * - ThreadUtil  — 线程休眠（内部已处理 InterruptedException）
 */
public class RandomSleepDemo {

    private static final int MAX_SLEEP_SECONDS = 60;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void main(String[] args) {
        System.out.println("========== Hutool 随机休眠演示 ==========");
        System.out.printf("随机范围: 0 ~ %d 秒%n%n", MAX_SLEEP_SECONDS);

        // 生成 0~60000 毫秒的随机休眠时间
        int sleepMs = RandomUtil.randomInt(0, MAX_SLEEP_SECONDS * 1000 + 1);

        String startTime = LocalTime.now().format(TIME_FORMAT);
        System.out.printf("[%s] 开始休眠，时长: %d ms (%.1f 秒)%n",
                startTime, sleepMs, sleepMs / 1000.0);

        // Hutool 的 ThreadUtil.sleep()：
        // - 无需 try-catch（内部已捕获 InterruptedException）
        // - 自动恢复线程中断标志位
        ThreadUtil.sleep(sleepMs);

        String endTime = LocalTime.now().format(TIME_FORMAT);
        System.out.printf("[%s] 休眠结束！%n", endTime);
        System.out.println("=========================================");
    }
}
