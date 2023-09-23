package ru.stm.auth.repository.impl;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import ru.stm.auth.repository.RedisRepository;

@Slf4j
@Primary
@Repository
public class RedisRepositoryImpl implements RedisRepository {

    private static final Pattern CRLF = Pattern.compile(" *\n *");

    private final StringRedisTemplate template;

    private ValueOperations<String, String> valueOperations;

    private final ObjectMapper objectMapper;

    @Autowired
    public RedisRepositoryImpl(@Qualifier("configuredRedisTemplate") StringRedisTemplate template, ObjectMapper objectMapper) {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initialize() {
        valueOperations = template.opsForValue();
    }

    @Override
    public <T> T getObject(String key, String suffix, Class<T> clazz) {
        return internalGetObject(key, suffix, clazz);
    }

    @Override
    public <T> T getObject(String key, String suffix, TypeReference<T> typeReference) {
        return internalGetObject(key, suffix, typeReference);
    }

    @Override
    public <T> T getObject(String key, RedisSuffixes suffix, Class<T> clazz) {
        return getObject(key, suffix.getSuffix(), clazz);
    }

    @Override
    public <T> T getObject(String key, RedisSuffixes suffix, TypeReference<T> typeReference) {
        return getObject(key, suffix.getSuffix(), typeReference);
    }

    @Override
    public <T> T getObjectAndUpdateTtl(String key, String suffix, long ttlSeconds, Class<T> clazz) {
        T object = internalGetObject(key, suffix, clazz);
        if (nonNull(object)) {
            updateTtl(key, suffix, ttlSeconds);
        }
        return object;
    }

    @Override
    public <T> T getObjectAndUpdateTtl(String key, String suffix, long ttlSeconds, TypeReference<T> typeReference) {
        T object = internalGetObject(key, suffix, typeReference);
        if (nonNull(object)) {
            updateTtl(key, suffix, ttlSeconds);
        }
        return object;
    }

    @Override
    public <T> T getObjectAndUpdateTtl(String key, RedisSuffixes suffix, long ttlSeconds, Class<T> clazz) {
        return getObjectAndUpdateTtl(key, suffix.getSuffix(), ttlSeconds, clazz);
    }

    @Override
    public <T> T getObjectAndUpdateTtl(String key, RedisSuffixes suffix, long ttlSeconds, TypeReference<T> typeReference) {
        return getObjectAndUpdateTtl(key, suffix.getSuffix(), ttlSeconds, typeReference);
    }

    @Override
    public Map<String, String> getKeysAndValuesByPatternAndUpdateTtl(String pattern, RedisSuffixes suffix, long ttlSeconds) {
        List<String> keysMatchingThePattern = new ArrayList<>(template.keys(pattern));
        log.debug("{} keys were read by pattern [{}] from redis", keysMatchingThePattern.size(), pattern);
        List<String> multiValuesFromRedis = valueOperations.multiGet(keysMatchingThePattern);
        Map<String, String> keysAndValuesFromRedis = new HashMap<>();
        for (int i = 0; i < keysMatchingThePattern.size(); i++) {
            keysAndValuesFromRedis.put(keysMatchingThePattern.get(i), multiValuesFromRedis.get(i));
        }
        return keysAndValuesFromRedis;
    }

    @Override
    public String getString(String key) {
        return internalGetString(key);
    }

    @Override
    public String getString(String key, String suffix) {
        String fullKey = buildFullKey(null, key, suffix);
        return internalGetString(fullKey);
    }

    @Override
    public String getString(String key, RedisSuffixes suffix) {
        return getString(key, suffix.getSuffix());
    }

    @Override
    public String getStringAndUpdateTtl(String key, long ttlSeconds) {
        return internalGetStringAndUpdateTtl(key, ttlSeconds);
    }

    @Override
    public String getStringAndUpdateTtl(String key, RedisSuffixes suffix, long ttlSeconds) {
        return getStringAndUpdateTtl(key, suffix.getSuffix(), ttlSeconds);
    }

    @Override
    public String getStringAndUpdateTtl(String key, String suffix, long ttlSeconds) {
        String fullKey = buildFullKey(null, key, suffix);
        return internalGetStringAndUpdateTtl(fullKey, ttlSeconds);

    }

    @Override
    public void setObject(String key, Object value) {
        internalSetObject(key, value);
    }

    @Override
    public void setObject(String key, String suffix, Object value) {
        internalSetObject(buildFullKey(null, key, suffix), value);
    }

    @Override
    public void setObject(String key, RedisSuffixes suffix, Object value) {
        setObject(key, suffix.getSuffix(), value);
    }

    @Override
    public void setObject(String key, String suffix, Object value, long ttlSeconds) {
        try {
            String objectAsJsonString = objectMapper.writeValueAsString(value);
            internalSetString(buildFullKey(null, key, suffix), objectAsJsonString, ttlSeconds);
        } catch (JsonProcessingException e) {
            throw new CannotSerializeObjectToString(e, value);
        }
    }

    @Override
    public void setObject(String key, RedisSuffixes suffix, Object value, long ttlSeconds) {
        setObject(key, suffix.getSuffix(), value, ttlSeconds);
    }

    @Override
    public void setString(String key, String value) {
        internalSetString(key, value);
    }

    @Override
    public void setString(String key, String suffix, String value) {
        internalSetString(buildFullKey(null, key, suffix), value);
    }

    @Override
    public void setString(String key, RedisSuffixes suffix, String value) {
        setString(key, suffix.getSuffix(), value);
    }

    @Override
    public void setString(String key, String value, long ttlSeconds) {
        internalSetString(key, value, ttlSeconds);
    }

    @Override
    public void setString(String key, String suffix, String value, long ttlSeconds) {
        internalSetString(buildFullKey(null, key, suffix), value, ttlSeconds);
    }

    @Override
    public void setString(String key, RedisSuffixes suffix, String value, long ttlSeconds) {
        setString(key, suffix.getSuffix(), value, ttlSeconds);
    }

    @Override
    public Boolean delString(String key) {
        log.debug("Deleting key={} from redis", key);
        return template.delete(key);
    }

    @Override
    public Boolean delString(String key, String suffix) {
        return delString(buildFullKey(null, key, suffix));
    }

    @Override
    public Boolean delString(String key, RedisSuffixes suffix) {
        return delString(key, suffix.getSuffix());
    }

    @Override
    public Boolean updateTtl(String key, long ttlSeconds) {
        log.debug("Update TTL to {} seconds for key {}", ttlSeconds, key);
        return template.expire(key, ttlSeconds, SECONDS);
    }

    @Override
    public Boolean updateTtl(String key, String suffix, long ttlSeconds) {
        return updateTtl(buildFullKey(null, key, suffix), ttlSeconds);
    }

    @Override
    public Boolean updateTtl(String key, RedisSuffixes suffix, long ttlSeconds) {
        return updateTtl(key, suffix.getSuffix(), ttlSeconds);
    }

    public String buildFullKey(String keyPrefix, String key, String keySuffix) {
        StringBuilder fullKey = new StringBuilder();
        if (hasText(keyPrefix)) {
            fullKey.append(keyPrefix).append(".");
        }
        fullKey.append(key);
        if (hasText(keySuffix)) {
            fullKey.append(".").append(keySuffix);
        }
        return fullKey.toString();
    }

    private void internalSetString(String key, String value) {
        log.debug("Setting key={} with string {}", key, value);
        valueOperations.set(key, value);
    }

    private void internalSetString(String key, String value, long ttlSeconds) {
        log.debug("Setting key={} with TTL {} seconds and string {}", key, ttlSeconds, value);
        valueOperations.set(key, value, ttlSeconds, SECONDS);
    }

    private String internalGetString(String key) {
        String value = valueOperations.get(key);
        log.debug("For key={} received string {}", key, stringify(value));
        return value;
    }

    private String internalGetStringAndUpdateTtl(String key, long ttlSeconds) {
        String value = internalGetString(key);
        if (nonNull(value)) {
            updateTtl(key, ttlSeconds);
        }
        return value;
    }

    private <T> T internalGetObject(String key, String suffix, TypeReference<T> classReference) {
        String stringObject = internalGetString(buildFullKey(null, key, suffix));
        if (hasText(stringObject)) {
            try {
                return objectMapper.readValue(stringObject, classReference);
            } catch (IOException e) {
                throw new CannotDeserializeStringToObject(e, stringObject);
            }
        }
        return null;
    }

    private <T> T internalGetObject(String key, String suffix, Class<T> clazz) {
        String stringObject = internalGetString(buildFullKey(null, key, suffix));
        if (hasText(stringObject)) {
            try {
                return objectMapper.readValue(stringObject, clazz);
            } catch (IOException e) {
                throw new CannotDeserializeStringToObject(e, stringObject);
            }
        }
        return null;
    }

    private void internalSetObject(String key, Object value) {
        try {
            String objectAsJsonString = objectMapper.writeValueAsString(value);
            internalSetString(key, objectAsJsonString);
        } catch (JsonProcessingException e) {
            throw new CannotSerializeObjectToString(e, value);
        }
    }

    private String stringify(String value) {
        return ofNullable(value).map(e -> CRLF.matcher(e).replaceAll(" ")).orElse(null);
    }

}
