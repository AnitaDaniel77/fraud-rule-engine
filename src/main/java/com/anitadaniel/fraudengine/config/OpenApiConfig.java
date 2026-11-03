package com.anitadaniel.fraudengine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Spring reads this class for setup
public class OpenApiConfig {

    @Bean // the object returned here is managed by Spring
    public OpenAPI fraudEngineApi() {
        Info info = new Info()
                .title("Fraud Rule Engine API")
                .version("1.0")
                .description("Submit transactions, run fraud rules on each one, and read back the flags.");
        return new OpenAPI().info(info); // the title and description shown at the top of the page
    }
}