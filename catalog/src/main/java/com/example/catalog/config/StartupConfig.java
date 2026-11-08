package com.example.catalog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

    private static final Logger log = LoggerFactory.getLogger(StartupConfig.class);

    // This bean is created ONLY if catalog.startup-banner=true.
    @Bean
    @ConditionalOnProperty(name = "catalog.startup-banner", havingValue = "true")
    public CommandLineRunner startupBanner(CatalogProperties props) {
        return args -> log.info("*** {} started with startup banner enabled ***", props.name());
    }
}