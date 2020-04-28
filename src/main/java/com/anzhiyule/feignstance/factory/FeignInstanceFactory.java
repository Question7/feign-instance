package com.anzhiyule.feignstance.factory;

import com.anzhiyule.feignstance.annotation.FeignInstance;
import com.anzhiyule.feignstance.exception.FeignInstanceException;
import com.anzhiyule.feignstance.handler.FeignInvocationHandler;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.*;

public class FeignInstanceFactory {

    private final ApplicationContext context;

    public FeignInstanceFactory(ApplicationContext context){
        this.context = context;
    }

    public <T> T getInstance(Class<T> clazz) {
        if (clazz.getAnnotation(FeignInstance.class) != null) {
            Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, FeignInvocationHandler.createInvocationHandler(context, clazz));
            return (T)o;
        } else {
            throw new FeignInstanceException("[" + clazz.getName() + "] is not " + FeignInstance.class);
        }
    }
}
