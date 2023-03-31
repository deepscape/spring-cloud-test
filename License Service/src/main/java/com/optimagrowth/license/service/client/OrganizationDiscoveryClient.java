package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OrganizationDiscoveryClient {

    // spring cloud load balancer 와 상호 작용
    private final DiscoveryClient discoveryClient;

    public Organization getOrganization(String organizationId) {
        RestTemplate restTemplate = new RestTemplate();

        // 유레카에 등록된 organization service 의 모든 instance list 를 얻는다.
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");

        if (instances.size() == 0) return null;

        // service endpoint 를 검색해서 가져온다.
        String serviceUri = String.format("%s/v1/organization/%s", instances.get(0).getUri().toString(), organizationId);

        // service 호출을 위해 표준 스프링 RestTemplate Class 사용
        ResponseEntity<Organization> restExchange = restTemplate.exchange(serviceUri, HttpMethod.GET, null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
