package com.optimagrowth.gateway.filters;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FilterUtils {

    public static final String CORRELATION_ID = "tmx-correlation-id";
    // public static final String AUTH_TOKEN     = "tmx-auth-token";
    public static final String AUTH_TOKEN     = "Authorization";
    public static final String USER_ID        = "tmx-user-id";
    public static final String ORG_ID         = "tmx-org-id";
    public static final String PRE_FILTER_TYPE = "pre";
    public static final String POST_FILTER_TYPE = "post";
    public static final String ROUTE_FILTER_TYPE = "route";

    // tmx-correlation-id 가 존재한다면 출력하기 위한 용도
    public String getCorrelationId(HttpHeaders requestHeaders){
        if (requestHeaders.get(CORRELATION_ID) !=null) {
            List<String> header = requestHeaders.get(CORRELATION_ID);
            return header.stream().findFirst().get();
        }
        else{
            return null;
        }
    }

    // requestHeader 에 tmx-correlation-id 입력
    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

    public String getAuthToken(HttpHeaders requestHeaders){
        if (requestHeaders.get(AUTH_TOKEN) !=null) {
            List<String> header = requestHeaders.get(AUTH_TOKEN);
            return header.stream().findFirst().get();
        }
        else{
            return null;
        }
    }

}
