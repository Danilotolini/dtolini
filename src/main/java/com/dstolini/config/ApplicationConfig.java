package com.dstolini.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestClient;

@Configuration
class ApplicationConfig {

    @Bean
    OpenAPI openApiInfo() {
        return new OpenAPI().info(new Info()
                .title("DTolini API")
                .description("Calcula YTM, preco limpo, preco sujo e juro acumulado para Treasuries americanas.")
                .version("0.1.0")
                .contact(new Contact().name("Danilo Tolini").email("danilotollini@gmail.com")));
    }

    @Bean
    RestClient restClient(@Value("${treasury.api.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("ingest-retry-");
        scheduler.initialize();
        return scheduler;
    }
}
