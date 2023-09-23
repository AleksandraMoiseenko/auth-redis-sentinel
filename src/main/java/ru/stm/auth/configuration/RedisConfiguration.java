package ru.stm.auth.configuration;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfiguration {

    @Primary
    @Bean("lettuceConnectionFactory")
    public RedisConnectionFactory lettuceConnectionFactory(RedisProperties properties) {

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
            .master(properties.getMaster());

        var ips = properties.getNodes().stream().map(s -> new SentinelIp(s.split(":"))).collect(
            Collectors.toList());

        ips.forEach(s ->
            sentinelConfig.sentinel(s.getIp(), s.getPort()));

        sentinelConfig.setDatabase(1);

        return new LettuceConnectionFactory(sentinelConfig);
    }

    @Primary
    @Bean("configuredRedisTemplate")
    public StringRedisTemplate redisTemplate(@Qualifier("lettuceConnectionFactory") RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @AllArgsConstructor
    @Data
    private class SentinelIp {
        private String ip;
        private int port;

        public SentinelIp (String[] data) {
            this.ip = data[0];
            this.port = Integer.parseInt(data[1]);
        }
    }
}
