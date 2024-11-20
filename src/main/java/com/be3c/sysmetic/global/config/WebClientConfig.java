package com.be3c.sysmetic.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${email.stibee.access}")
    private String accessToken;

    @Bean
    public WebClient webClientForStibeeApi() {
        return WebClient.builder()
                .baseUrl("https://api.stibee.com/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader("AccessToken", accessToken)
                .build();
    }
}