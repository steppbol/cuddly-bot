package com.balashenka.cuddly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = "com.balashenka.cuddly")
public class WebConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
