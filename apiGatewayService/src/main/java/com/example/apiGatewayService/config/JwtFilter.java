package com.example.apiGatewayService.config;


import jakarta.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter {

    @Autowired
    private WebClient.Builder webClient;

    private final Logger log = LoggerFactory.getLogger(JwtFilter.class);


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Filter:{}", exchange.getRequest().getURI());
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        if (path.contains("/auth/login")) {
            log.info("For login api there is no authentication");
            return chain.filter(exchange);
        }
        String authToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authToken == null || authToken.isBlank()) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        log.info("Authentication token: {}", authToken);
        return webClient.build().get().uri("http://authentication-service/auth/validate?token=" + authToken)
                .retrieve().bodyToMono(Boolean.class)
                .flatMap(valid -> {
                    if (valid) {
                        log.info("Token is authenticates {}", true);
                        return chain.filter(exchange);
                    } else {
                        log.info("Un authorized");
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        return response.setComplete();
                    }
                });
    }

}
