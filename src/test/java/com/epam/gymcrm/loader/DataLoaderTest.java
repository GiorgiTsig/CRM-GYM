package com.epam.gymcrm.loader;

import com.epam.gymcrm.loader.interfaces.GymDataLoader;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class DataLoaderTest {

    @Test
    void loadAllDoesNotInvokeLoadersWhenDisabledLoop() {
        DataLoader dataLoader = new DataLoader();
        GymDataLoader loader = mock(GymDataLoader.class);
        ReflectionTestUtils.setField(dataLoader, "loaders", List.of(loader));

        dataLoader.loadAll();

        verifyNoInteractions(loader);
    }
}
