package com.example.gridtrade.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public abstract class FeignAbstractFallbackFactory<T> implements FallbackFactory<T> {
    @Override
    public T create(Throwable cause) {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class<?> clazz;
        try {
            clazz = Class.forName(actualTypeArguments[0].getTypeName());
            log.warn("{},request fail,msg:{}", clazz.getSimpleName(), cause.getMessage(), cause);
        } catch (ClassNotFoundException e) {
            log.warn("FeignAbstractFallbackFactory,find class fail", e);
            log.warn("request fail,msg:{},", cause.getMessage(), cause);
        }
        return null;
    }
}
