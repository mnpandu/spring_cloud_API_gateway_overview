package com.example.springcloudgatewayoverview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());
        return restTemplate;
    }

    private List<HttpMessageConverter<?>> getMessageConverters() {
        // Customize the message converters here
        return Collections.singletonList(new MappingJackson2HttpMessageConverter());
    }
}