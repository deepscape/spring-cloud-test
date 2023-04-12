package com.optimagrowth.license.service.client;

import com.optimagrowth.license.respository.OrganizationRedisRepository;
import com.optimagrowth.license.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.optimagrowth.license.model.Organization;

@RequiredArgsConstructor
@Component
public class OrganizationRestTemplateClient {

    private final KeycloakRestTemplate restTemplate;
    private final OrganizationRedisRepository redisRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    private Organization checkRedisCache(String organizationId) {
        try {
            return redisRepository.findById(organizationId).orElse(null);
        } catch (Exception e) {
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache. Exception {}", organizationId, e);
            return null;
        }
    }

    private void cacheOrganizationObject(Organization organization) {
        try {
            redisRepository.save(organization);
        } catch (Exception e) {
            logger.error("Unable to cache organization {} in Redis. Exception {}", organization.getId(), e);
        }
    }

    public Organization getOrganization(String organizationId){
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

        // redis 에서 cache data 조회
        Organization organization = checkRedisCache(organizationId);

        if (organization != null) {
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}", organizationId, organization);
            return organization;
        }

        logger.debug("Unable to locate organization from the redis cache: {}.", organizationId);

        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        "http://localhost:8072/organization/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        // Save the record to redis
        organization = restExchange.getBody();
        if (organization != null) {
            cacheOrganizationObject(organization);
        }

        return restExchange.getBody();
    }

}