package com.trinov.qrCodeApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
public class QrCodeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrCodeApiApplication.class, args);
    }

    @Bean
    public SecurityWebFilterChain securityConfiguration(ServerHttpSecurity http) {
        return http.csrf().disable().cors().and().build();
    }
}

