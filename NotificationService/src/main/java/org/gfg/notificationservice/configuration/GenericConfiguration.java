package org.gfg.notificationservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper(); //CONVERT STRING TO JSON AND VICE VERSA
    }
}
