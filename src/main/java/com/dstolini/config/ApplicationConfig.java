package com.dstolini.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ApplicationConfig {

    @Bean
    RestClient restClient(@Value("${treasury.api.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
