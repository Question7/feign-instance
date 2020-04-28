package com.anzhiyule.feignstance.configuration;

import com.anzhiyule.feignstance.factory.FeignInstanceFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignInstanceAutoConfiguration {

    @Bean
    public FeignInstanceFactory feignInstanceFactory(ApplicationContext context) {
        return new FeignInstanceFactory(context);
    }
}
