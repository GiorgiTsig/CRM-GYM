package com.epam.gymcrm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.*;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Import({DatabaseConfig.class})
@ComponentScan(
        basePackages = "com.epam.gymcrm",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = RestController.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = EnableWebMvc.class)
        }
)
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public static MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
