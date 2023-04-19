package com.optimagrowth.gateway.filters;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Configuration
public class ResponseFilter {

    final Logger logger =LoggerFactory.getLogger(ResponseFilter.class);

    private final Tracer tracer;
    private final FilterUtils filterUtils;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // sleuth traceId 의 응답 헤더 tmx-correlation-id 에 span 추가
                String traceId = tracer.currentSpan().context().traceIdString();
                logger.debug("Adding the correlation id to the outbound headers. {}", traceId);

                exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);
                logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
            }));

//            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
//
//                String correlationId = filterUtils.getCorrelationId(requestHeaders);
//                logger.debug("Adding the correlation id to the outbound headers. {}", correlationId);
//
//                exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);
//                logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
//            }));
        };
    }
}