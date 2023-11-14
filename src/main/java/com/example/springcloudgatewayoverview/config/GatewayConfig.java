package com.example.springcloudgatewayoverview.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.WebFilter;

import com.example.springcloudgatewayoverview.filter.AuthFilter;
import com.example.springcloudgatewayoverview.filter.PostGlobalFilter;
import com.example.springcloudgatewayoverview.filter.RequestFilter;

@Configuration
public class GatewayConfig {

    @Autowired
    RequestFilter requestFilter;

    @Autowired
    AuthFilter authFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                // adding 2 rotes to first microservice as we need to log request body if method is POST
        return builder.routes()
                .route("first-microservice",r -> r.path("/first")
                        .and().readBody(Object.class, s -> true).filters(f -> f.filters(requestFilter, authFilter))
                        .uri("http://localhost:8081"))
                .route("auth-server",r -> r.path("/login")
                        .uri("http://localhost:8088"))
                .build();
    }

    
    
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebFilter responseFilter(){
        return new PostGlobalFilter();
    }

}
