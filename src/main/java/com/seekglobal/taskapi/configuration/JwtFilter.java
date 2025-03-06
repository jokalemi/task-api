package com.seekglobal.taskapi.configuration;

import com.seekglobal.taskapi.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtFilter implements WebFilter {
    @Value("${security.allowed-paths}")
    private List<String> allowedPaths;

    @Value("${spring.webflux.base-path}")
    private String basePath;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        if (allowedPath(path))
            return chain.filter(exchange);
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null)
            return Mono.error(new ForbiddenException("No token was found"));
        if (!auth.startsWith("Bearer "))
            return Mono.error(new ForbiddenException("Invalid auth"));
        String token = auth.replace("Bearer ", "");
        exchange.getAttributes().put("token", token);
        return chain.filter(exchange);
    }

    private boolean allowedPath(String path) {
        if (basePath != null && !basePath.isEmpty() && path.startsWith(basePath)) {
            path = path.substring(basePath.length());
        }
        return allowedPaths.stream().anyMatch(path::startsWith);
    }
}

