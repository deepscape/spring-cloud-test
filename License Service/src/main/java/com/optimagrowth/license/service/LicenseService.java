package com.optimagrowth.license.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.respository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import com.optimagrowth.license.utils.UserContextHolder;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.optimagrowth.license.model.License;


@RequiredArgsConstructor
@Service
public class LicenseService {

    final MessageSource messages;
    final private LicenseRepository licenseRepository;
    final ServiceConfig config;
    final OrganizationDiscoveryClient organizationDiscoveryClient;
    final OrganizationRestTemplateClient organizationRestClient;
    final OrganizationFeignClient organizationFeignClient;

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    public License getLicense(String licenseId, String organizationId, String clientType) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        if (null == license) {
            throw new IllegalArgumentException(String.format(messages.getMessage("license.search.error.message %s %s", null, null), licenseId, organizationId));
        }

        Organization organization = retrieveOrganizationInfo(organizationId, clientType);

        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }

    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {

        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }

    public License createLicense(License license){
        license.withComment(config.getProperty());
        licenseRepository.save(license);

        return license;
    }

    public License updateLicense(License license){
        license.withComment(config.getProperty());
        licenseRepository.save(license);

        return license;
    }

    public String deleteLicense(String licenseId){
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);

        responseMessage = String.format(messages.getMessage("license.delete.message %s", null, null),licenseId);

        return responseMessage;
    }

    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @Retry(name="retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
    @Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "buildFallbackLicenseList" )
    public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
        logger.debug("getLicensesByOrganization Correlation id: {}", UserContextHolder.getContext().getCorrelationId());
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    @SuppressWarnings("unused")
    private List<License> buildFallbackLicenseList(String organizationId, Throwable t){
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }


    @CircuitBreaker(name = "organizationService")
    public Organization getOrganization(String organizationId) throws TimeoutException {
        randomlyRunLong();
        return organizationRestClient.getOrganization(organizationId);
    }

    private void randomlyRunLong() throws TimeoutException {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        if (randomNum==3) sleep();
    }

    private void sleep() throws TimeoutException {
        try {
            System.out.println("Sleep");
            Thread.sleep(5000);
            throw new java.util.concurrent.TimeoutException();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }
}