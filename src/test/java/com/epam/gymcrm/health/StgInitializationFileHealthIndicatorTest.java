package com.epam.gymcrm.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StgInitializationFileHealthIndicatorTest {

    @Test
    void health_returnsUp_whenFileExistsAndReadable() throws IOException {
        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getFilename()).thenReturn("initial-data.json");
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));

        StgInitializationFileHealthIndicator healthIndicator =
                new StgInitializationFileHealthIndicator(resource, "classpath:initial-data.json");

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("classpath:initial-data.json", health.getDetails().get("path"));
    }

    @Test
    void health_returnsDown_whenFileMissing() {
        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(false);

        StgInitializationFileHealthIndicator healthIndicator =
                new StgInitializationFileHealthIndicator(resource, "classpath:initial-data.json");

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Initialization file is missing", health.getDetails().get("reason"));
    }

    @Test
    void health_returnsDown_whenFileReadFails() throws IOException {
        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("cannot read"));

        StgInitializationFileHealthIndicator healthIndicator =
                new StgInitializationFileHealthIndicator(resource, "classpath:initial-data.json");

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("error"));
    }
}

