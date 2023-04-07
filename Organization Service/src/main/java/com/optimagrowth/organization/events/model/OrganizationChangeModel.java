package com.optimagrowth.organization.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrganizationChangeModel {
    private String type;
    private String action;              // 이벤트를 발생시킨 액션
    private String organizationId;      // 이벤트와 연관된 조직 ID
    private String correlationId;       // 이벤트를 발생시킨 서비스 호출의 상관관계 ID (중요)

    public OrganizationChangeModel(String type, String action, String organizationId, String correlationId) {
        // super();
        this.type = type;
        this.action = action;
        this.organizationId = organizationId;
        this.correlationId = correlationId;
    }
}
