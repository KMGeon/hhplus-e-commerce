package kr.hhplus.be.server.config;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestcontainersConfiguration {

    public static final MySQLContainer<?> MYSQL_CONTAINER;
    public static final GenericContainer<?> REDIS_CONTAINER;
    public static final KafkaContainer KAFKA_CONTAINER;

    private static final int REDIS_PORT = 6379;

    static {
        // MySQL 컨테이너 설정
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("hhplus")
                .withUsername("test")
                .withPassword("test");
        MYSQL_CONTAINER.start();

        System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());

        // JPA 설정
        System.setProperty("spring.jpa.hibernate.ddl-auto", "create");
        System.setProperty("spring.sql.init.mode", "always");
        System.setProperty("spring.sql.init.data-locations", "classpath:setup.sql");
        System.setProperty("spring.jpa.defer-datasource-initialization", "true");
        System.setProperty("spring.jpa.show-sql", "true");

        // Redis 컨테이너 설정
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:latest"))
                .withExposedPorts(REDIS_PORT)
                .withCommand("redis-server", "--appendonly", "yes");
        REDIS_CONTAINER.start();

        String redisHost = REDIS_CONTAINER.getHost();
        Integer redisMappedPort = REDIS_CONTAINER.getMappedPort(REDIS_PORT);
        System.setProperty("spring.data.redis.host", redisHost);
        System.setProperty("spring.data.redis.port", String.valueOf(redisMappedPort));

        System.setProperty("spring.redis.redisson.config",
                String.format("singleServerConfig:\n" +
                        "  address: \"redis://%s:%d\"\n" +
                        "  connectionMinimumIdleSize: 1\n" +
                        "  connectionPoolSize: 10\n" +
                        "  connectTimeout: 10000\n" +
                        "  timeout: 3000", redisHost, redisMappedPort));

        // 🚀 Kafka 컨테이너 설정
        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
                .withKraft(); // KRaft 모드 사용 (Zookeeper 불필요)
        KAFKA_CONTAINER.start();

        // Kafka Spring Boot 속성 설정
        System.setProperty("spring.kafka.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers());

        // Producer 설정
        System.setProperty("spring.kafka.producer.key-serializer", "org.apache.kafka.common.serialization.StringSerializer");
        System.setProperty("spring.kafka.producer.value-serializer", "org.apache.kafka.common.serialization.StringSerializer");
        System.setProperty("spring.kafka.producer.acks", "all");
        System.setProperty("spring.kafka.producer.retries", "3");

        // Consumer 설정
        System.setProperty("spring.kafka.consumer.key-deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        System.setProperty("spring.kafka.consumer.value-deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        System.setProperty("spring.kafka.consumer.group-id", "test-group");
        System.setProperty("spring.kafka.consumer.auto-offset-reset", "earliest");
        System.setProperty("spring.kafka.consumer.enable-auto-commit", "false");

        // Admin 설정 (토픽 자동 생성용)
        System.setProperty("spring.kafka.admin.properties.bootstrap.servers", KAFKA_CONTAINER.getBootstrapServers());
    }

    @PreDestroy
    public void preDestroy() {
        // 💡 모든 컨테이너 정리
        if (MYSQL_CONTAINER != null && MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.stop();
        }
        if (REDIS_CONTAINER != null && REDIS_CONTAINER.isRunning()) {
            REDIS_CONTAINER.stop();
        }
        if (KAFKA_CONTAINER != null && KAFKA_CONTAINER.isRunning()) {
            KAFKA_CONTAINER.stop();
        }
    }
}