# HKD Exchange - Spring Boot 项目模板

本文档提供标准化的Spring Boot 3.2项目结构和配置模板。

---

## 目录

1. [Maven多模块结构](#maven多模块结构)
2. [父POM配置](#父pom配置)
3. [子模块配置](#子模块配置)
4. [Application配置](#application配置)
5. [项目结构](#项目结构)
6. [常用依赖](#常用依赖)

---

## Maven多模块结构

### 标准模块划分

```
user-service/
├── pom.xml                 # 父POM
├── user-api/               # API接口定义
│   ├── pom.xml
│   └── src/main/java/
├── user-domain/            # 领域模型
│   ├── pom.xml
│   └── src/main/java/
├── user-application/       # 业务逻辑
│   ├── pom.xml
│   └── src/main/java/
├── user-infrastructure/    # 基础设施
│   ├── pom.xml
│   └── src/main/java/
└── user-bootstrap/         # 启动模块
    ├── pom.xml
    └── src/main/java/
```

### 模块职责

| 模块 | 职责 | 依赖关系 |
|------|------|---------|
| **api** | DTO、Request/Response对象 | hkd-common |
| **domain** | 实体、值对象、领域服务 | api, hkd-common |
| **application** | 业务逻辑、应用服务 | domain |
| **infrastructure** | 数据库、缓存、MQ等实现 | domain, application |
| **bootstrap** | 启动类、配置类 | 所有模块 |

---

## 父POM配置

### user-service/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.hkd.user</groupId>
    <artifactId>user-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>HKD User Service</name>
    <description>HKD Exchange User Service</description>

    <modules>
        <module>user-api</module>
        <module>user-domain</module>
        <module>user-application</module>
        <module>user-infrastructure</module>
        <module>user-bootstrap</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <!-- Spring Boot -->
        <spring-boot.version>3.2.0</spring-boot.version>

        <!-- HKD Common -->
        <hkd-common.version>1.0.0-SNAPSHOT</hkd-common.version>

        <!-- Database -->
        <postgresql.version>42.7.1</postgresql.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <flyway.version>10.2.0</flyway.version>

        <!-- Redis -->
        <redisson.version>3.25.2</redisson.version>

        <!-- Security -->
        <jjwt.version>0.12.3</jjwt.version>
        <bouncycastle.version>1.77</bouncycastle.version>

        <!-- Utilities -->
        <lombok.version>1.18.30</lombok.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <guava.version>32.1.3-jre</guava.version>

        <!-- Testing -->
        <junit.version>5.10.1</junit.version>
        <mockito.version>5.8.0</mockito.version>
        <testcontainers.version>1.19.3</testcontainers.version>

        <!-- Plugins -->
        <maven-compiler-plugin.version>3.11.0</maven-compiler-plugin.version>
        <maven-surefire-plugin.version>3.2.2</maven-surefire-plugin.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot BOM -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- HKD Common -->
            <dependency>
                <groupId>com.hkd</groupId>
                <artifactId>hkd-common</artifactId>
                <version>${hkd-common.version}</version>
            </dependency>

            <!-- Internal Modules -->
            <dependency>
                <groupId>com.hkd.user</groupId>
                <artifactId>user-api</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.hkd.user</groupId>
                <artifactId>user-domain</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.hkd.user</groupId>
                <artifactId>user-application</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.hkd.user</groupId>
                <artifactId>user-infrastructure</artifactId>
                <version>${project.version}</version>
            </dependency>

            <!-- PostgreSQL -->
            <dependency>
                <groupId>org.postgresql</groupId>
                <artifactId>postgresql</artifactId>
                <version>${postgresql.version}</version>
            </dependency>

            <!-- MyBatis Plus -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>

            <!-- Flyway -->
            <dependency>
                <groupId>org.flywaydb</groupId>
                <artifactId>flyway-core</artifactId>
                <version>${flyway.version}</version>
            </dependency>
            <dependency>
                <groupId>org.flywaydb</groupId>
                <artifactId>flyway-database-postgresql</artifactId>
                <version>${flyway.version}</version>
            </dependency>

            <!-- Redisson -->
            <dependency>
                <groupId>org.redisson</groupId>
                <artifactId>redisson-spring-boot-starter</artifactId>
                <version>${redisson.version}</version>
            </dependency>

            <!-- JWT -->
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
            </dependency>

            <!-- Bouncy Castle -->
            <dependency>
                <groupId>org.bouncycastle</groupId>
                <artifactId>bcprov-jdk18on</artifactId>
                <version>${bouncycastle.version}</version>
            </dependency>

            <!-- MapStruct -->
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>

            <!-- Guava -->
            <dependency>
                <groupId>com.google.guava</groupId>
                <artifactId>guava</artifactId>
                <version>${guava.version}</version>
            </dependency>

            <!-- Testcontainers -->
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>${testcontainers.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- Lombok (所有模块都需要) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- SLF4J -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>

        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Mockito -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                </plugin>

                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>${maven-compiler-plugin.version}</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <encoding>${project.build.sourceEncoding}</encoding>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                            <path>
                                <groupId>org.mapstruct</groupId>
                                <artifactId>mapstruct-processor</artifactId>
                                <version>${mapstruct.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>

                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>${maven-surefire-plugin.version}</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

---

## 子模块配置

### user-api/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.hkd.user</groupId>
        <artifactId>user-service</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>user-api</artifactId>

    <name>User API</name>
    <description>User Service API Definitions</description>

    <dependencies>
        <!-- HKD Common -->
        <dependency>
            <groupId>com.hkd</groupId>
            <artifactId>hkd-common</artifactId>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
    </dependencies>
</project>
```

### user-domain/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.hkd.user</groupId>
        <artifactId>user-service</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>user-domain</artifactId>

    <name>User Domain</name>
    <description>User Service Domain Models</description>

    <dependencies>
        <!-- User API -->
        <dependency>
            <groupId>com.hkd.user</groupId>
            <artifactId>user-api</artifactId>
        </dependency>

        <!-- HKD Common -->
        <dependency>
            <groupId>com.hkd</groupId>
            <artifactId>hkd-common</artifactId>
        </dependency>

        <!-- MyBatis Plus Annotations -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-annotation</artifactId>
        </dependency>
    </dependencies>
</project>
```

### user-application/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.hkd.user</groupId>
        <artifactId>user-service</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>user-application</artifactId>

    <name>User Application</name>
    <description>User Service Application Logic</description>

    <dependencies>
        <!-- User Domain -->
        <dependency>
            <groupId>com.hkd.user</groupId>
            <artifactId>user-domain</artifactId>
        </dependency>

        <!-- Spring Context -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </dependency>

        <!-- Spring Transaction -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
        </dependency>

        <!-- Guava -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>
    </dependencies>
</project>
```

### user-infrastructure/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.hkd.user</groupId>
        <artifactId>user-service</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>user-infrastructure</artifactId>

    <name>User Infrastructure</name>
    <description>User Service Infrastructure</description>

    <dependencies>
        <!-- User Application -->
        <dependency>
            <groupId>com.hkd.user</groupId>
            <artifactId>user-application</artifactId>
        </dependency>

        <!-- PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>

        <!-- MyBatis Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>

        <!-- Flyway -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>

        <!-- Redisson -->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
        </dependency>

        <!-- Spring Data Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- Testcontainers for Integration Tests -->
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### user-bootstrap/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.hkd.user</groupId>
        <artifactId>user-service</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>user-bootstrap</artifactId>

    <name>User Bootstrap</name>
    <description>User Service Bootstrap Application</description>

    <dependencies>
        <!-- User Infrastructure -->
        <dependency>
            <groupId>com.hkd.user</groupId>
            <artifactId>user-infrastructure</artifactId>
        </dependency>

        <!-- Spring Boot Starter Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Starter Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Spring Boot Actuator -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Micrometer Prometheus -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- Spring Boot DevTools (Optional) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Application配置

### application.yml

```yaml
spring:
  application:
    name: user-service

  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:hkd_user}
    username: ${DB_USERNAME:hkd_admin}
    password: ${DB_PASSWORD:hkd_dev_password_2024}
    hikari:
      maximum-pool-size: ${DB_POOL_SIZE:10}
      minimum-idle: ${DB_MIN_IDLE:5}
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:hkd_redis_2024}
      database: ${REDIS_DB:0}
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 2
          max-wait: -1ms

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    encoding: UTF-8

server:
  port: ${SERVER_PORT:8001}
  shutdown: graceful
  tomcat:
    threads:
      max: 200
      min-spare: 10

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  metrics:
    export:
      prometheus:
        enabled: true
  health:
    redis:
      enabled: true
    db:
      enabled: true

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath*:mapper/**/*.xml

hkd:
  worker-id: ${WORKER_ID:0}
  security:
    jwt:
      secret: ${JWT_SECRET:hkd_jwt_secret_key_please_change_in_production}
      access-token-expire: 3600
      refresh-token-expire: 604800

logging:
  level:
    root: INFO
    com.hkd: DEBUG
    com.baomidou.mybatisplus: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/user-service.log
```

### application-dev.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hkd_user
    username: hkd_admin
    password: hkd_dev_password_2024

  data:
    redis:
      host: localhost
      port: 6379
      password: hkd_redis_2024

logging:
  level:
    com.hkd: DEBUG
```

### application-prod.yml

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10

logging:
  level:
    root: WARN
    com.hkd: INFO
```

---

## 项目结构

### user-api

```
user-api/src/main/java/com/hkd/user/api/
├── dto/
│   ├── UserDTO.java
│   ├── UserProfileDTO.java
│   └── ...
├── request/
│   ├── UserRegisterRequest.java
│   ├── UserLoginRequest.java
│   └── ...
└── response/
    ├── UserRegisterResponse.java
    ├── UserLoginResponse.java
    └── ...
```

### user-domain

```
user-domain/src/main/java/com/hkd/user/domain/
├── entity/
│   ├── User.java
│   ├── UserProfile.java
│   └── ...
├── repository/
│   ├── UserRepository.java         # 接口
│   └── ...
├── service/
│   ├── UserDomainService.java      # 领域服务
│   └── ...
└── valueobject/
    ├── Email.java
    ├── PhoneNumber.java
    └── ...
```

### user-application

```
user-application/src/main/java/com/hkd/user/application/
├── service/
│   ├── UserApplicationService.java
│   ├── UserQueryService.java
│   └── ...
└── assembler/
    ├── UserAssembler.java          # DTO <-> Entity 转换
    └── ...
```

### user-infrastructure

```
user-infrastructure/src/main/java/com/hkd/user/infrastructure/
├── persistence/
│   ├── mapper/
│   │   ├── UserMapper.java
│   │   └── ...
│   ├── po/
│   │   ├── UserPO.java
│   │   └── ...
│   └── repository/
│       ├── UserRepositoryImpl.java # Repository接口实现
│       └── ...
├── cache/
│   ├── UserCacheService.java
│   └── ...
└── mq/
    ├── UserEventPublisher.java
    └── ...
```

```
user-infrastructure/src/main/resources/
├── db/
│   └── migration/
│       ├── V1__create_user_table.sql
│       ├── V2__add_user_profile.sql
│       └── ...
└── mapper/
    ├── UserMapper.xml
    └── ...
```

### user-bootstrap

```
user-bootstrap/src/main/java/com/hkd/user/
├── UserServiceApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── RedisConfig.java
│   └── ...
└── controller/
    ├── UserController.java
    └── ...
```

---

## 常用依赖

### 数据库相关

```xml
<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- MyBatis Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
</dependency>

<!-- Flyway -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

### 缓存相关

```xml
<!-- Redisson -->
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
</dependency>

<!-- Spring Data Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 安全相关

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 监控相关

```xml
<!-- Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Prometheus -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

---

## 版本历史

- **v1.0** (2024-11-17): 初始版本，提供Spring Boot 3.2项目模板

---

Generated with Claude Code
