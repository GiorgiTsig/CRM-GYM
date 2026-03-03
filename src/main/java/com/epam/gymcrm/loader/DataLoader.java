package com.epam.gymcrm.loader;

import com.epam.gymcrm.loader.interfaces.GymDataLoader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

@Component
@DependsOn("dataSourceInitializer")
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private List<GymDataLoader> loaders;

    @Autowired
    public void setLoaders(List<GymDataLoader> loaders) {
        this.loaders = loaders;
    }

    @PostConstruct
    public void loadAll() {
        try {
//            loaders.forEach(GymDataLoader::load);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize", e);
        }
    }
}
