## 주의사항

1. Hoxton.SR9 / SpringBoot 2.3.x 버전 호환 주의할 것        <- 버전 안맞으면 Test 과정에서 에러
: https://minholee93.tistory.com/entry/ERROR-ClassNotFoundException-orgspringframeworkbootcontextpropertiesConfigurationBeanFactoryMetadata

2. SpringBoot 2.3.x 이상부터 Layered Jar 지원 주의할 것
3. com.spotify 버전 1.4.4 에서 에러 발생 안 함        <- 자바 1.8 로 고정되어야 함

## Docker 
docker run -it -p8080:8080 deeepscape/licensing-service:0.0.1-SNAPSHOT
