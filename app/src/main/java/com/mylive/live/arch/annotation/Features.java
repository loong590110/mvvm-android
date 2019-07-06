package com.mylive.live.arch.annotation;

import com.mylive.live.arch.feature.Feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by zailongshi on 2019/7/6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Features {
    Class<? extends Feature>[] value();
}
