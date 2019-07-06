package com.mylive.live.arch.feature;

import com.mylive.live.arch.annotation.Features;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * Create by zailongshi on 2019/7/6
 */
public final class FeaturesFinder {

    public static void findAll(Class host, FindAll findAll) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(findAll);
        Annotation[] annotations = host.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Features) {
                findAll.all(((Features) annotation).value());
                break;
            }
        }
    }

    public static void findEach(Class host, FindEach findEach) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(findEach);
        findAll(host, classes -> {
            for (Class<? extends Feature> clazz : classes) {
                findEach.each(clazz);
            }
        });
    }

    public interface FindAll {
        void all(Class<? extends Feature>[] classes);
    }

    public interface FindEach {
        void each(Class<? extends Feature> clazz);
    }
}
