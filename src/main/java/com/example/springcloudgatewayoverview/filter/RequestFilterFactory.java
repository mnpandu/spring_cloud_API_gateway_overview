package com.example.springcloudgatewayoverview.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestFilterFactory extends AbstractGatewayFilterFactory<RequestFilterFactory.Config> {

    public RequestFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Your custom filter logic goes here
        return (exchange, chain) -> {
            // Implement your filter logic before the request is forwarded
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Configuration properties for your filter
    }
}