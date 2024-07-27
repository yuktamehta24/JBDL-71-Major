package org.gfg.userservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericConfig {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper(); //CONVERT STRING TO JSON AND VICE VERSA
    }
}
