package com.optimagrowth.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

@RefreshScope    // Config Server 변경되면, Refresh
// 본 서비스가 Source Class 에 정의된 채널을 이용해 메시지 브로커와 통신(발신)할 것이라고 spring cloud stream 에 알리는 역할
@EnableBinding(Source.class)
@SpringBootApplication
public class OrganizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationServiceApplication.class, args);
    }

}