package com.epam.gymcrm.loader;

import com.fasterxml.jackson.databind.JsonNode;

public interface GymDataLoader {
    void load();
    void processData(JsonNode rootNode);
}
