package com.epam.gymcrm.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebConfigTest {

    @Test
    void webConfig_declaresExpectedAnnotations() {
        assertNotNull(WebConfig.class.getAnnotation(Configuration.class));
        assertNotNull(WebConfig.class.getAnnotation(EnableWebMvc.class));

        ComponentScan componentScan = WebConfig.class.getAnnotation(ComponentScan.class);
        assertNotNull(componentScan);
        assertArrayEquals(new String[]{"com.epam.gymcrm.restController"}, componentScan.basePackages());
    }
}

