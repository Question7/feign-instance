package com.anzhiyule.feignstance.handler;

import com.anzhiyule.feignstance.annotation.Filter;
import com.anzhiyule.feignstance.annotation.Path;
import com.anzhiyule.feignstance.annotation.Proxy;
import com.anzhiyule.feignstance.exception.FeignInstanceException;
import com.anzhiyule.feignstance.filter.PreRequestFilter;
import com.anzhiyule.feignstance.request.FeignRequest;
import com.anzhiyule.feignstance.request.HttpMethod;
import com.anzhiyule.feignstance.request.ProxyMeta;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class FeignInvocationHandler implements InvocationHandler {

    private static Logger LOGGER = Logger.getLogger(FeignInvocationHandler.class.getName());

    private RestInvocation restInvocation;

    private String baseUrl;

    private List<PreRequestFilter> filters;

    private ProxyMeta proxyMeta;

    private HttpMethod method;

    private FeignInvocationHandler(RestInvocation restInvocation, String baseUrl, HttpMethod method, List<PreRequestFilter> filters, ProxyMeta proxyMeta) {
        this.restInvocation = restInvocation;
        this.baseUrl = baseUrl;
        this.filters = filters;
        this.proxyMeta = proxyMeta;
        this.method = method;
    }

    public static FeignInvocationHandler createInvocationHandler(ApplicationContext context, Class clazz) {
        String path = "";
        HttpMethod method = HttpMethod.GET;
        ProxyMeta proxyMeta = ProxyMeta.emptyProxy();
        List<PreRequestFilter> filters = new LinkedList<>();

        Path classPathAnnotation = (Path) clazz.getAnnotation(Path.class);
        if (classPathAnnotation != null) {
            path = classPathAnnotation.value();
            method = classPathAnnotation.method();
        }

        Proxy classProxyAnnotation = (Proxy)clazz.getAnnotation(Proxy.class);
        if (classProxyAnnotation != null) {
            proxyMeta.setHost(classProxyAnnotation.host());
            proxyMeta.setPort(classProxyAnnotation.port());
        }

        Filter classFilterAnnotation = (Filter)clazz.getAnnotation(Filter.class);
        if (classFilterAnnotation != null) {
            Class<? extends PreRequestFilter>[] classes = classFilterAnnotation.values();
            LOGGER.info(String.format("filter total %s", classes.length));
            for (Class<? extends PreRequestFilter> filterClass : classes) {
                PreRequestFilter o = context.getBean(filterClass);
                if (o == null) {
                    throw new FeignInstanceException("Not found bean with class [" + filterClass.getName() + "]. should be registered to spring ioc.");
                } else {
                    filters.add(o);
                }
            }
        }

        return new FeignInvocationHandler(new RestInvocation(), path, method, filters, proxyMeta);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        FeignRequest feignRequest = restInvocation.createRequest(this.baseUrl, this.method, this.proxyMeta, this.filters, method, args);

        String result = restInvocation.processRequest(feignRequest);
        Class<?> returnType = method.getReturnType();

        return restInvocation.adaptResult(result, returnType);
    }
}
