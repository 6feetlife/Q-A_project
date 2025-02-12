package com.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
// JPA 활성화
@EnableJpaAuditing
public class SpringStartApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringStartApplication.class, args);
  }
}
