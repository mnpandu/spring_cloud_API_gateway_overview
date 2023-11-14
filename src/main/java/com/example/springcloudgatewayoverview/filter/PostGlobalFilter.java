package com.example.springcloudgatewayoverview.filter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PostGlobalFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        ServerHttpResponse response = exchange.getResponse();
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-CorrelationId");
        
        if (correlationId == null) {
			correlationId = generateCorrelationId();
		}
        exchange.getRequest().mutate().header("X-CorrelationId", correlationId);
        
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = getDecoratedResponse(path, correlationId,response, dataBufferFactory);
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private ServerHttpResponseDecorator getDecoratedResponse(String path, String correlationId,ServerHttpResponse response,  DataBufferFactory dataBufferFactory) {
        return new ServerHttpResponseDecorator(response) {

            @Override
            public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {

                if (body instanceof Flux) {

                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                        DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffers.readableByteCount()];
                        joinedBuffers.read(content);
                        String responseBody = new String(content, StandardCharsets.UTF_8);//MODIFY RESPONSE and Return the Modified response
                        System.out.println("CorrelationId ="+correlationId+ " ; Response body :"+ responseBody);
                        //response.getHeaders().add("X-CorrelationId", correlationId);
                        return dataBufferFactory.wrap(responseBody.getBytes());
                    })).onErrorResume(err -> {

                        System.out.println("error while decorating Response: {}"+err.getMessage());
                        return Mono.empty();
                    });

                }
                return super.writeWith(body);
            }
        };
    }
    
    private String generateCorrelationId() {
		// Implement your logic to generate a unique correlation ID
		// This could be a UUID or any other mechanism that ensures uniqueness
		return UUID.randomUUID().toString();
	}
    
}
