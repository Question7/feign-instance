package com.anzhiyule.feignstance.request;

public enum HttpMethod {

    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE");

    public String value;

    HttpMethod(String value){
        this.value = value;
    }
}
