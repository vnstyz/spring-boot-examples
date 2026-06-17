package org.example.mysqlredistest.redis;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class RedisConnectionTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisConnectionTestApplication.class, args);
    }

    @Bean
    public ApplicationRunner redisTestRunner(StringRedisTemplate redisTemplate) {
        return args -> {
            String key = "redis:connect:test";
            String value = "Redis test at " + LocalDateTime.now();
            redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));

            String readBack = redisTemplate.opsForValue().get(key);
            String ping;
            try (var connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()) {
                ping = connection.ping();
            }

            System.out.println("========== Redis TEST ==========");
            System.out.println("PING = " + ping);
            System.out.println("SET/GET success. " + key + " = " + readBack);
            System.out.println("================================");
        };
    }
}
