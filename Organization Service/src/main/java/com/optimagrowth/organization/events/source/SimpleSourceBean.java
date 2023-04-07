package com.optimagrowth.organization.events.source;

import com.optimagrowth.organization.events.model.OrganizationChangeModel;
import com.optimagrowth.organization.utils.ActionEnum;
import com.optimagrowth.organization.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SimpleSourceBean {
    private final Source source;
    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    public void publishOrganizationChange(ActionEnum action, String organizationId) {
        logger.debug("Sending kafka message {} for Organization Id: {}", action, organizationId);
        OrganizationChangeModel change = new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action.toString(),
                organizationId,
                UserContext.getCorrelationId()
        );

        source.output().send(MessageBuilder.withPayload(change).build());
    }

}
