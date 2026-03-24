package com.epam.gymcrm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    private final AppConfig appConfig = new AppConfig();

    @Test
    void objectMapper_registersJavaTimeModuleAndDisablesTimestampSerialization() {
        ObjectMapper objectMapper = appConfig.objectMapper();

        assertNotNull(objectMapper.findModules());
        assertFalse(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }

    @Test
    void methodValidationPostProcessor_returnsBeanInstance() {
        MethodValidationPostProcessor processor = AppConfig.methodValidationPostProcessor();

        assertNotNull(processor);
    }
}

