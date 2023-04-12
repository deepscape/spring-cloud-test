package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Id;

@Getter @Setter @ToString
@RedisHash("organization")      // 조직 데이터가 저장될 레디스 서버의 해시 이름을 설정한다.
public class Organization extends RepresentationModel<Organization> {

    @Id
    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;
}
