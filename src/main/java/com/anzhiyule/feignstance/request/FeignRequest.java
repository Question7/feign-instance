package com.anzhiyule.feignstance.request;

import javax.validation.constraints.NotEmpty;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FeignRequest {

    public FeignRequest(String path) throws MalformedURLException {
        this.url = new URL(path);
        this.method = HttpMethod.GET;
    }

    public FeignRequest(URL url) {
        this.url = url;
        this.method = HttpMethod.GET;
    }

    public FeignRequest(String path, HttpMethod method) throws MalformedURLException {
        this.url = new URL(path);
        this.method = method;
    }

    public FeignRequest(URL url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    private URL url;

    private HttpMethod method;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, Object> params = new HashMap<>();

    private Object body;

    private ProxyMeta proxyMeta;


    public void addHeader(@NotEmpty String key, String value) {
        headers.put(key, value);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public URL getUrl() {
        return this.url;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public Object getBody() {
        return this.body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public ProxyMeta getProxyMeta() {
        return this.proxyMeta;
    }

    public void setProxyMeta(ProxyMeta proxyMeta) {
        this.proxyMeta = proxyMeta;
    }
}
