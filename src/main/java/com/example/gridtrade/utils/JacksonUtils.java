package com.example.gridtrade.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Slf4j
public class JacksonUtils {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对象转json
     *
     * @param object 对象
     * @return
     */
    @SneakyThrows
    public static String writeValueAsString(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        return MAPPER.writeValueAsString(object);
    }

    /**
     * json转对象
     *
     * @param content   json
     * @param valueType 对象类型
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T readValue(String content, Class<T> valueType) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return MAPPER.readValue(content, valueType);
    }

    /**
     * json转对象
     *
     * @param content
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return MAPPER.readValue(content, valueTypeRef);
    }

    /**
     * 对象转json
     *
     * @param object       对象
     * @param defaultValue 默认值
     * @return
     */
    public static String writeValueAsStringDefault(Object object, String defaultValue) {
        try {
            return StringUtils.defaultString(writeValueAsString(object), defaultValue);
        } catch (Exception e) {
            log.warn("writeValueAsStringDefault,fail", e);
            return defaultValue;
        }
    }

    /**
     * json转对象
     *
     * @param content      json
     * @param valueType    对象类型
     * @param defaultValue 默认值
     * @param <T>
     * @return
     */
    public static <T> T readValueDefault(String content, Class<T> valueType, T defaultValue) {
        try {
            return ObjectUtils.defaultIfNull(readValue(content, valueType), defaultValue);
        } catch (Exception e) {
            log.warn("readValueDefault,fail", e);
            return defaultValue;
        }
    }

    /**
     * 简单检查数据是否为json字符串
     *
     * @param data
     * @return
     */
    public static boolean isJsonSimpleCheck(String data) {
        if (StringUtils.isBlank(data)) {
            return false;
        }
        if (StringUtils.startsWith(data, "{") && StringUtils.endsWith(data, "}")) {
            return true;
        }
        if (StringUtils.startsWith(data, "[") && StringUtils.endsWith(data, "]")) {
            return true;
        }
        return false;
    }

    /**
     * json value转换为对象
     *
     * @param fromValue
     * @param toValueType
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueType) {
        if (Objects.isNull(fromValue)) {
            return null;
        }
        return MAPPER.convertValue(fromValue, toValueType);
    }

    /**
     * json value转换为对象
     *
     * @param fromValue
     * @param toValueType
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T convertValueDefault(Object fromValue, TypeReference<T> toValueType, T defaultValue) {
        try {
            return ObjectUtils.defaultIfNull(convertValue(fromValue, toValueType), defaultValue);
        } catch (Exception e) {
            log.warn("convertValueDefault,fail", e);
            return defaultValue;
        }
    }
}

