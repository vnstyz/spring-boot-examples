package org.example.mysqlredistest.mysql;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication(exclude = RedisAutoConfiguration.class)
public class MysqlConnectionTestApplication {

    private static final Logger log = LoggerFactory.getLogger(MysqlConnectionTestApplication.class);

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS mysql_connect_test (
                id         BIGINT       PRIMARY KEY AUTO_INCREMENT,
                message    VARCHAR(100) NOT NULL,
                created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

    public static void main(String[] args) {
        SpringApplication.run(MysqlConnectionTestApplication.class, args);
    }

    @Bean
    public ApplicationRunner mysqlTestRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute(CREATE_TABLE_SQL);

                String message = "MySQL test at " + LocalDateTime.now().format(FORMATTER);
                jdbcTemplate.update("INSERT INTO mysql_connect_test(message) VALUES (?)", message);

                Map<String, Object> row = jdbcTemplate.queryForMap("""
                        SELECT id, message, created_at
                        FROM mysql_connect_test
                        ORDER BY id DESC
                        LIMIT 1
                        """);

                log.info("========== MySQL TEST ==========");
                log.info("Insert success. latest row = {}", row);
                log.info("================================");
            } catch (Exception e) {
                log.error("========== MySQL TEST FAILED ==========");
                log.error("MySQL 连接测试失败: {}", e.getMessage());
                log.error("================================", e);
            }
        };
    }
}
