package com.anzhiyule.feignstance.handler;

import com.alibaba.fastjson.JSONObject;
import com.anzhiyule.feignstance.annotation.*;
import com.anzhiyule.feignstance.annotation.Proxy;
import com.anzhiyule.feignstance.exception.FeignInstanceException;
import com.anzhiyule.feignstance.request.FeignRequest;
import com.anzhiyule.feignstance.filter.PreRequestFilter;
import com.anzhiyule.feignstance.request.HttpMethod;
import com.anzhiyule.feignstance.request.ProxyMeta;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.*;
import java.util.*;

public class RestInvocation {

    public FeignRequest createRequest(String baseUrl, HttpMethod requestMethod, ProxyMeta proxyMeta, List<PreRequestFilter> filters, Method method, Object[] args) {
        FeignRequest feignRequest;
        try {
            feignRequest = baseRequest(baseUrl, requestMethod, proxyMeta, method, args);
        } catch (MalformedURLException e) {
            throw new FeignInstanceException("Path url error.", e);
        }
        filterRequest(feignRequest, filters);
        return feignRequest;
    }

    public String processRequest(FeignRequest feignRequest) throws IOException {
        URL url = feignRequest.getUrl();
        ProxyMeta proxyMeta = feignRequest.getProxyMeta();
        HttpURLConnection connection;
        if (proxyMeta != null && proxyMeta.getHost() != null) {
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyMeta.getHost(), proxyMeta.getPort()));
            connection = (HttpURLConnection) url.openConnection(proxy);
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }
        defaultProperties(connection);
        connection.setRequestMethod(feignRequest.getMethod().value);
        appendProperties(connection, feignRequest);
        connection.connect();
        Map<String, Object> params = feignRequest.getParams();
        Object body = feignRequest.getBody();
        String result;
        if (body != null) {
            result = writeConnection(connection, JSONObject.toJSONString(body).getBytes("UTF-8"));
        } else if (params != null && !params.isEmpty()) {
            result = writeConnection(connection, JSONObject.toJSONString(params).getBytes("UTF-8"));
        } else {
            result = readConnection(connection);
        }
        connection.disconnect();
        return result;
    }

    private void defaultProperties(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setDoOutput(true);
        connection.setDoInput(true);
    }

    private void appendProperties(HttpURLConnection connection, FeignRequest request) {
        request.getHeaders().forEach(connection::setRequestProperty);
    }

    private FeignRequest baseRequest(String baseUrl, HttpMethod requestMethod, ProxyMeta proxyMeta, Method method, Object[] args) throws MalformedURLException {
        Path methodPathAnnotation = method.getAnnotation(Path.class);

        if (methodPathAnnotation != null) {
            baseUrl += methodPathAnnotation.value();
            requestMethod = methodPathAnnotation.method();
        }
        return request(baseUrl, requestMethod, proxyMeta, method, args);
    }

    private void filterRequest(FeignRequest feignRequest, List<PreRequestFilter> filters) {
        filters.forEach(f -> f.filter(feignRequest));
    }

    private String readConnection(HttpURLConnection connection) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = connection.getInputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            int status = connection.getResponseCode();
            if (status != 200) {
                throw new FeignInstanceException(status + " , " + connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new FeignInstanceException("The net occurs error on send feign request.", e);
        }
        return sb.toString();
    }

    private String writeConnection(HttpURLConnection connection, byte[] data) {
        try (OutputStream os = connection.getOutputStream()) {
            os.write(data);
        } catch (IOException e) {
            throw new FeignInstanceException("", e);
        }
        return readConnection(connection);
    }

    private FeignRequest request(String path, HttpMethod requestMethod, ProxyMeta proxyMeta, Method method, Object[] args) throws MalformedURLException {
        path += "?";
        Map<String, Object> params = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        Object body = null;
        for (int i = 0; i < parameters.length; i ++) {
            Parameter parameter = parameters[i];
            URLParam urlParam = parameter.getAnnotation(URLParam.class);
            if (urlParam != null) {
                Object o = args[i];
                o = o instanceof String ? URLEncoder.encode((String) o) : o;
                path += urlParam.value() + "=" + o + "&";
            }
            FormParam formParam = parameter.getAnnotation(FormParam.class);
            if (formParam != null) {
                params.put(formParam.value(), args[i]);
            }
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
            if (requestBody != null) {
                body = args[i];
            }
        }

        Proxy proxy = method.getAnnotation(Proxy.class);
        if (proxy != null) {
            proxyMeta.setHost(proxy.host());
            proxyMeta.setPort(proxy.port());
        }
        FeignRequest feignRequest = new FeignRequest(path, requestMethod);
        feignRequest.setParams(params);
        feignRequest.setBody(body);
        feignRequest.setProxyMeta(proxyMeta);
        return feignRequest;
    }

    public Object adaptResult(String s, Class<?> returnType) {
        if (returnType == String.class) {
            return s;
        } else if (returnType == Integer.class) {
            return Integer.parseInt(s);
        } else if (returnType == Long.class) {
            return Long.parseLong(s);
        } else if (returnType == Double.class) {
            return Double.parseDouble(s);
        } else if (returnType == Boolean.class) {
            return Boolean.getBoolean(s);
        } else if (returnType == Short.class) {
            return Short.parseShort(s);
        } else if (returnType == Byte.class) {
            return Byte.parseByte(s);
        } else if (returnType == Character.class) {
            return s.charAt(0);
        } else {
            return JSONObject.parseObject(s, returnType);
        }
    }
}
