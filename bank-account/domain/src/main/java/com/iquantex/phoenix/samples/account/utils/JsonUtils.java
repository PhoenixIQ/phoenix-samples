package com.iquantex.phoenix.samples.account.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baozi
 * @date 2020/5/27 10:52 AM
 */
@Slf4j
public class JsonUtils {

    private final static ObjectMapper       objectMapper = new ObjectMapper();

    private final static Map<String, Class> cacheClass   = new ConcurrentHashMap<>();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    private JsonUtils() {
    }

    public static String encode(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonGenerationException e) {
            log.error("encode(Object)", e); //$NON-NLS-1$
        } catch (JsonMappingException e) {
            log.error("encode(Object)", e); //$NON-NLS-1$
        } catch (IOException e) {
            log.error("encode(Object)", e); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * 将json string反序列化成对象
     * @param json
     * @param valueType
     * @return
     */
    public static <T> T decode(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonParseException e) {
            log.error("decode(String, Class<T>)", e);
        } catch (JsonMappingException e) {
            log.error("decode(String, Class<T>)", e);
        } catch (IOException e) {
            log.error("decode(String, Class<T>)", e);
        }
        return null;
    }

    /**
     * 将json string反序列化成对象
     * @param json
     * @param className
     * @return
     */
    public static <T> T decode(String json, String className) {
        try {
            Class<T> valueType = getClass(className);
            return decode(json, valueType);
        } catch (ClassNotFoundException e) {
            log.error("decode(String, String)", e);
        }
        return null;
    }

    /**
     * 将json array反序列化为对象
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T decode(String json, TypeReference<T> typeReference) {
        try {
            return (T) objectMapper.readValue(json, typeReference);
        } catch (JsonParseException e) {
            log.error("decode(String, JsonTypeReference<T>)", e);
        } catch (JsonMappingException e) {
            log.error("decode(String, JsonTypeReference<T>)", e);
        } catch (IOException e) {
            log.error("decode(String, JsonTypeReference<T>)", e);
        }
        return null;
    }

    private static Class getClass(String className) throws ClassNotFoundException {
        Class aClass = cacheClass.get(className);
        if (aClass == null) {
            aClass = Class.forName(className);
            cacheClass.put(className, aClass);
        }
        return aClass;
    }

}