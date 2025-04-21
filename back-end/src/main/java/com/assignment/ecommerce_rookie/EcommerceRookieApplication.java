package com.assignment.ecommerce_rookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EcommerceRookieApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceRookieApplication.class, args);
    }

}
