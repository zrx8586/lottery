package com.example.demo.lottery.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

/**
 * @author long_w
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 注册 JavaTimeModule 以支持 Java 8 日期/时间类型
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 将对象序列化为 JSON 字符串
     * @param object 要序列化的对象
     * @return JSON 字符串
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为对象
     * @param json JSON 字符串
     * @param clazz 目标对象的类
     * @param <T> 泛型类型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为 List<T> 对象
     * @param json JSON 字符串
     * @param clazz 目标对象的类
     * @param <T> 泛型类型
     * @return 反序列化后的 List<T> 对象
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化为 List 失败", e);
        }
    }
}