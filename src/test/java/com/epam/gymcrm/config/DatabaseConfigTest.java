package com.epam.gymcrm.config;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    private static final String URL = "jdbc:mysql://localhost:3306/db";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private static final String ENTITIES = "com.epam.gymcrm.domain";

    private DatabaseConfig config;

    @BeforeEach
    void setUp() {
        config = new DatabaseConfig();
        ReflectionTestUtils.setField(config, "datasourceUrl", URL);
        ReflectionTestUtils.setField(config, "driverName", DRIVER);
        ReflectionTestUtils.setField(config, "username", USERNAME);
        ReflectionTestUtils.setField(config, "password", PASSWORD);
        ReflectionTestUtils.setField(config, "entities", ENTITIES);
        ReflectionTestUtils.setField(config, "initialize", true);
    }

    @Test
    void dataSourceUsesProvidedConnectionProperties() {
        BasicDataSource dataSource = (BasicDataSource) config.dataSource();

        assertEquals(DRIVER, dataSource.getDriverClassName());
        assertEquals(URL, dataSource.getUrl());
        assertEquals(USERNAME, dataSource.getUsername());
        assertEquals(PASSWORD, dataSource.getPassword());
    }

    @Test
    void dataSourceInitializerRespectsInitializeFlag() {
        DataSource dataSource = config.dataSource();

        DataSourceInitializer initializer = config.dataSourceInitializer(dataSource);

        assertEquals(Boolean.TRUE, ReflectionTestUtils.getField(initializer, "enabled"));
        assertSame(dataSource, ReflectionTestUtils.getField(initializer, "dataSource"));
        assertNotNull(ReflectionTestUtils.getField(initializer, "databasePopulator"));

        ReflectionTestUtils.setField(config, "initialize", false);
        DataSourceInitializer disabled = config.dataSourceInitializer(dataSource);
        assertEquals(Boolean.FALSE, ReflectionTestUtils.getField(disabled, "enabled"));
    }

    @Test
    void entityManagerFactoryConfiguredWithPackagesAndJpaProperties() {
        DataSource dataSource = config.dataSource();

        LocalContainerEntityManagerFactoryBean factory = config.entityManagerFactory(dataSource);

        var props = factory.getJpaPropertyMap();
        assertEquals("org.hibernate.dialect.MySQL8Dialect", props.get("hibernate.dialect"));
        assertEquals("update", props.get("hibernate.hbm2ddl.auto"));
        assertEquals("true", props.get("hibernate.show_sql"));
    }
}
