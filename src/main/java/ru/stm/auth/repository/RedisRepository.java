package ru.stm.auth.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import ru.stm.auth.repository.impl.RedisSuffixes;

public interface RedisRepository {

    <T> T getObject(String key, String suffix, Class<T> clazz);

    <T> T getObject(String key, String suffix, TypeReference<T> typeReference);

    <T> T getObject(String key, RedisSuffixes suffix, Class<T> clazz);

    <T> T getObject(String key, RedisSuffixes suffix, TypeReference<T> typeReference);

    <T> T getObjectAndUpdateTtl(String key, String suffix, long ttlSeconds, Class<T> clazz);

    <T> T getObjectAndUpdateTtl(String key, String suffix, long ttlSeconds, TypeReference<T> typeReference);

    <T> T getObjectAndUpdateTtl(String key, RedisSuffixes suffix, long ttlSeconds, Class<T> clazz);

    <T> T getObjectAndUpdateTtl(String key, RedisSuffixes suffix, long ttlSeconds, TypeReference<T> typeReference);

    Map<String, String> getKeysAndValuesByPatternAndUpdateTtl(String pattern, RedisSuffixes suffix, long ttlSeconds);

    String getString(String key);

    String getString(String key, String suffix);

    String getString(String key, RedisSuffixes suffix);

    String getStringAndUpdateTtl(String key, long ttlSeconds);

    String getStringAndUpdateTtl(String key, RedisSuffixes suffix, long ttlSeconds);

    String getStringAndUpdateTtl(String key, String suffix, long ttlSeconds);

    void setObject(String key, Object value);

    void setObject(String key, String suffix, Object value);

    void setObject(String key, RedisSuffixes suffix, Object value);

    void setObject(String key, String suffix, Object value, long ttlSeconds);

    void setObject(String key, RedisSuffixes suffix, Object value, long ttlSeconds);

    void setString(String key, String value);

    void setString(String key, String suffix, String value);

    void setString(String key, RedisSuffixes suffix, String value);

    void setString(String key, String value, long ttlSeconds);

    void setString(String key, String suffix, String value, long ttlSeconds);

    void setString(String key, RedisSuffixes suffix, String value, long ttlSeconds);

    Boolean delString(String key);

    Boolean delString(String key, String suffix);

    Boolean delString(String key, RedisSuffixes suffix);

    Boolean updateTtl(String key, long ttlSeconds);

    Boolean updateTtl(String key, String suffix, long ttlSeconds);

    Boolean updateTtl(String key, RedisSuffixes suffix, long ttlSeconds);
}
