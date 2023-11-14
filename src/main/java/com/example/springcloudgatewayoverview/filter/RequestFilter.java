package com.example.springcloudgatewayoverview.filter;

import java.nio.charset.StandardCharsets;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RequestFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Flux<DataBuffer> body = exchange.getRequest().getBody();
        String fluxToString = fluxToString(body);
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-CorrelationId");
        System.out.println(correlationId+"-"+fluxToString);
        return chain.filter(exchange);
    }
    
    
    public static String fluxToString(Flux<DataBuffer> dataBufferFlux) {
		return DataBufferUtils.join(dataBufferFlux).map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
			buffer.read(bytes);
			DataBufferUtils.release(buffer);
			return new String(bytes, StandardCharsets.UTF_8);
		}).block();
	}
}
