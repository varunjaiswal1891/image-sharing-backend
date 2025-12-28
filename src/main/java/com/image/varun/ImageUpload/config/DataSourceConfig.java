package com.image.varun.ImageUpload.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "test")
public class DataSourceConfig {

    @Autowired
    private SecretsManagerConfig.DatabaseCredentials databaseCredentials;

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // Override with credentials from AWS Secrets Manager
        dataSource.setUsername(databaseCredentials.getUsername());
        dataSource.setPassword(databaseCredentials.getPassword());
        
        // If URL is provided in secrets, use it; otherwise use the one from properties
        if (databaseCredentials.getUrl() != null && !databaseCredentials.getUrl().isEmpty()) {
            dataSource.setJdbcUrl(databaseCredentials.getUrl());
        }

        return dataSource;
    }
}
