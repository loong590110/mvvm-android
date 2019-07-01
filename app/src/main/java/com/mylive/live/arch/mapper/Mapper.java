package com.mylive.live.arch.mapper;

import com.mylive.live.arch.annotation.FieldMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public final class Mapper {

    private Map<String, Object> map;

    public Mapper(Object from) {
        Objects.requireNonNull(from);
        map = new HashMap<>();
        for (Field field : from.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldMap.class)) {
                FieldMap fieldMap = field.getAnnotation(FieldMap.class);
                try {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    if (map.containsKey(fieldMap.value())) {
                        throw new IllegalArgumentException("Value "
                                + fieldMap.value()
                                + " already exists.");
                    }
                    map.put(fieldMap.value(), field.get(from));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Mapper from(Object from) {
        return new Mapper(from);
    }

    public void to(Object to) {
        Objects.requireNonNull(to);
        for (Field field : to.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldMap.class)) {
                FieldMap fieldMap = field.getAnnotation(FieldMap.class);
                try {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    field.set(to, map.get(fieldMap.value()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
