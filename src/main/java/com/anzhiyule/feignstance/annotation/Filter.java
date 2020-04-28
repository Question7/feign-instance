package com.anzhiyule.feignstance.annotation;

import com.anzhiyule.feignstance.filter.PreRequestFilter;

import javax.validation.constraints.NotEmpty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
    @NotEmpty
    Class<? extends PreRequestFilter>[] values();
}
