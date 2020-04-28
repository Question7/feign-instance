# feign-instance

### 调用方法的方式发送HTTP请求——接口的外部HTTP实现

## 使用说明

### 必要：
* Java 1.8
* Maven 3
* Spring 5
  
  
### 组件：
* Factory(工厂)  
    >`FeignInstanceFactory` 是一个spring `bean`，使用时可以通过注入获得。  
包含方法：`<T> T getInstance(Class<T> clazz)` 用来获取interface的实例。（参数`clazz`必须被`@FeignInstance`注解）。
  
* Filter(过滤器接口)  
    >`PreRequestFilter` 是一个标准接口。实现类需要是一个spring的`bean`。  
包含方法：`void filter(FeignRequest request)` 提供一个`FeignRequest`对象，用来对请求进行附加处理。  
  
* Annotation(注解)  
    >`@FeignInstance` :  
    >>作用位置：`interface`  
    作用：标识该接口使用外部HTTP实现  

    >`@Path`
    >>作用位置：`interface`, `method`  
    作用：标识该方法请求的URL路径  
    值1：`String value()`  
    值2：`HttpMethod method()`
    
    >`@Filter`
    >>作用位置：`interface`  
    作用：标识该接口中的所有方法需要通过该过滤器  
    值：`Class<? extends PreRequestFilter>[] values()` `NotEmpty`
    
    >`@Proxy`
    >>作用位置： `interface`, `method`  
    作用：标识该接口中的所有方法或该方法需要HTTP代理  
    值1：`String host()` `NotEmpty`  
    值2：`int port()` `NotEmpty`  
    
    >`@URLParam`
    >>作用位置：`method parameter`  
    作用：标识该请求的URL参数，会作为参数拼接到URL中  
    值：`String value()` `NotEmpty`  
    
    >`@FormParam`
    >>作用位置：`method parameter`  
    作用：标识该请求的表单参数，一般由多个简单类型组成  
    值：`String value()` `NotEmpty`  
    
    >`@RequestBody`
    >>作用位置：`method parameter`  
    作用：标识该请求的请求体，一个方法只可以有一个，并且一般是一个复杂类型，会转为JSON字符串发送  
  
  
### 例子：
`FeignService.java`
```java
@FeignInstance
@Proxy(host = "127.0.0.1", port = 1080)
@Filter(values = {TokenFilter.class})
@Path("http://www.anzhiyule.com")
public interface FeignService {
    
    @Path("/search")
    Map<String, String> query(@URLParam("wd") String word);
}
```  
`MyConsumer.java`
```java
public class MyConsumer {
    
    @Autowired
    private FeignInstanceFactory factory;
    
    public void process() {
        FeignService service = factory.getInstance(FeignService.class);
        Map<String, String> result = service.query("你好");
        result.foreach((k, v) -> System.out.println(k + ":" + v));
    }
}
```  
`TokenFilter`
```java
@Component
public class TokenFilter implements PreRequestFilter {
    
    @Override
    public void filter(FeignRequest request) {
        request.addHeader("Authorization", "Bearer ey...");
    }
}
```  
