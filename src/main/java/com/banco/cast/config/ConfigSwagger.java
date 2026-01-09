package com.banco.cast.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigSwagger {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banco API - Case Cast Solutions")
                        .description("Operações básicas de manipulação de contas bancárias.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Deise Souza")
                                .url("https://github.com/deisesouza")
                                .email("deise.santana.souza2018@gmail.com"))
                        .license(new License()
                                .name("Apache License Version 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}