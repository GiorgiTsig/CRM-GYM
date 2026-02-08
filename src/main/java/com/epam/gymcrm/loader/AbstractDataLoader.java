package com.epam.gymcrm.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class AbstractDataLoader implements GymDataLoader {

    private static final Logger log = LoggerFactory.getLogger(AbstractDataLoader.class);

    @Value("${storage.initialization.file.path}")
    private Resource resourceFile;

    protected ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void load() {
        if (!resourceFile.exists()) {
            log.warn("Initialization file not found: {}. Storage will be empty.", resourceFile.getFilename());
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resourceFile.getInputStream()))) {

            JsonNode rootNode = objectMapper.readTree(reader);
            processData(rootNode);
        } catch (IOException e) {
            log.error("Failed to load data from file: {}", resourceFile, e);
            throw new RuntimeException("Data loading failed", e);
        }
    }

    @Override
    public abstract void processData(JsonNode rootNode);
}
