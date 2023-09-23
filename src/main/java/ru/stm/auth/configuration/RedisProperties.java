package ru.stm.auth.configuration;

import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class RedisProperties {

    @Value("${spring.redis.sentinel.nodes}")
    private List<String> nodes;

    @Value("${spring.redis.sentinel.master}")
    private String master;
}
