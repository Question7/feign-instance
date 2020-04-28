package com.anzhiyule.feignstance.annotation;

import com.anzhiyule.feignstance.request.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    String value() default "";
    HttpMethod method() default HttpMethod.GET;
}
