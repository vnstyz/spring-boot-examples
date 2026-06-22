package com.vnstyz.controller;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MySQL 连接性能测试控制器。
 * <p>
 * 提供以下接口：
 * <ul>
 *   <li>GET /db/ping  —— 单次 SELECT 1，返回耗时</li>
 *   <li>GET /db/bench —— 批量压测，返回获取连接/查询/总耗时的统计</li>
 *   <li>GET /db/druid —— 返回 Druid 连接池实时监控数据</li>
 * </ul>
 */
@RestController
public class DbPerformanceController {

    private static final Logger log = LoggerFactory.getLogger(DbPerformanceController.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DbPerformanceController(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 启动时自动执行一次小规模连接性能测试，便于直接在日志中查看结果。
     */
    @Bean
    public ApplicationRunner dbStartupTest() {
        return args -> {
            log.info("========== 启动时 MySQL 连接性能测试 ==========");
            try {
                Map<String, Object> result = runBenchmark(20);
                log.info("启动测试结果(20次): {}", result);
            } catch (Exception e) {
                log.error("启动测试失败: {}", e.getMessage(), e);
            }
            log.info("=============================================");
        };
    }

    /**
     * 单次 ping：执行 SELECT 1 并返回耗时。
     */
    @GetMapping("/db/ping")
    public Map<String, Object> ping() {
        long t0 = System.nanoTime();
        Integer value = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        long t1 = System.nanoTime();
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("result", value);
        resp.put("costMs", round((t1 - t0) / 1_000_000.0));
        return resp;
    }

    /**
     * 批量压测：重复获取连接并执行 SELECT 1，统计耗时分布。
     *
     * @param iterations 测试次数，默认 50，范围 [1, 10000]
     */
    @GetMapping("/db/bench")
    public Map<String, Object> bench(@RequestParam(defaultValue = "50") int iterations) {
        iterations = Math.max(1, Math.min(iterations, 10000));
        return runBenchmark(iterations);
    }

    /**
     * 返回 Druid 连接池实时监控数据。
     */
    @GetMapping("/db/druid")
    public Map<String, Object> druid() {
        Map<String, Object> resp = new LinkedHashMap<>();
        if (dataSource instanceof DruidDataSource druid) {
            resp.put("activeCount", druid.getActiveCount());
            resp.put("poolingCount", druid.getPoolingCount());
            resp.put("poolingPeak", druid.getPoolingPeak());
            resp.put("activePeak", druid.getActivePeak());
            resp.put("connectCount", druid.getConnectCount());
            resp.put("closeCount", druid.getCloseCount());
            resp.put("maxActive", druid.getMaxActive());
        } else {
            resp.put("dataSource", dataSource.getClass().getName());
        }
        return resp;
    }

    /**
     * 执行一轮基准测试：分别统计 获取连接 / 执行查询 / 总耗时。
     */
    private Map<String, Object> runBenchmark(int iterations) {
        // 预热，避免首次加载驱动/建连影响统计
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        } catch (Exception ignored) {
            // 预热失败忽略，后续正式迭代会记录错误
        }

        List<Long> acquireTimes = new ArrayList<>(iterations);
        List<Long> queryTimes = new ArrayList<>(iterations);
        List<Long> totalTimes = new ArrayList<>(iterations);
        int errors = 0;

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            try (var conn = dataSource.getConnection()) {
                long acquired = System.nanoTime();
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                long done = System.nanoTime();
                acquireTimes.add(ms(acquired - start));
                queryTimes.add(ms(done - acquired));
                totalTimes.add(ms(done - start));
            } catch (Exception e) {
                errors++;
                if (errors <= 3) {
                    log.warn("第 {} 次迭代失败: {}", i + 1, e.getMessage());
                }
            }
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("iterations", iterations);
        resp.put("errors", errors);
        resp.put("acquireMs", stats(acquireTimes));
        resp.put("queryMs", stats(queryTimes));
        resp.put("totalMs", stats(totalTimes));
        return resp;
    }

    private static long ms(long nanos) {
        return Math.round(nanos / 1_000_000.0);
    }

    private static double round(double v) {
        return Math.round(v * 1000) / 1000.0;
    }

    private Map<String, Object> stats(List<Long> samples) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (samples.isEmpty()) {
            m.put("count", 0);
            return m;
        }
        List<Long> sorted = samples.stream().sorted().toList();
        DoubleSummaryStatistics s = samples.stream().mapToDouble(Long::doubleValue).summaryStatistics();
        m.put("count", s.getCount());
        m.put("minMs", s.getMin());
        m.put("maxMs", s.getMax());
        m.put("avgMs", round(s.getAverage()));
        m.put("p50Ms", sorted.get(sorted.size() / 2));
        int p95Idx = (int) Math.ceil(sorted.size() * 0.95) - 1;
        m.put("p95Ms", sorted.get(Math.max(0, p95Idx)));
        return m;
    }
}
