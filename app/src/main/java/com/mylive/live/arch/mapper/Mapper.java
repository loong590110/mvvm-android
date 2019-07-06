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

    private Map<String, Field> fromMap;
    private Map<String, Field> toMap;
    private Object from, to;

    private Mapper(Object from) {
        Objects.requireNonNull(from);
        this.from = from;
        fromMap = new HashMap<>();
        for (Field field : from.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldMap.class)) {
                FieldMap fieldMap = field.getAnnotation(FieldMap.class);
                if (fromMap.containsKey(fieldMap.value())) {
                    throw new IllegalArgumentException("Value "
                            + fieldMap.value()
                            + " already exists.");
                }
                fromMap.put(fieldMap.value(), field);
            }
        }
    }

    public static Mapper from(Object from) {
        return new Mapper(from);
    }

    public void to(Object to) {
        Objects.requireNonNull(to);
        if (this.to != null && this.to != to) {
            throw new IllegalArgumentException(this.to.toString() + " != " + to.toString());
        }
        this.to = to;
        if (toMap == null) {
            toMap = new HashMap<>();
            for (Field field : to.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(FieldMap.class)) {
                    FieldMap fieldMap = field.getAnnotation(FieldMap.class);
                    if (toMap.containsKey(fieldMap.value())) {
                        throw new IllegalArgumentException("Value "
                                + fieldMap.value()
                                + " already exists.");
                    }
                    toMap.put(fieldMap.value(), field);
                }
            }
        }
        for (Map.Entry<String, Field> entry : toMap.entrySet()) {
            Field fromField = fromMap.get(entry.getKey());
            if (fromField != null) {
                Field toField = entry.getValue();
                if (!fromField.isAccessible()) {
                    fromField.setAccessible(true);
                }
                if (!toField.isAccessible()) {
                    toField.setAccessible(true);
                }
                try {
                    toField.set(to, fromField.get(from));
                } catch (IllegalAccessException ignore) {
                }
            }
        }
    }
}
