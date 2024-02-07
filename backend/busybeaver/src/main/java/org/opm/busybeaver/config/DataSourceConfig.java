package org.opm.busybeaver.config;

import org.opm.busybeaver.enums.EnvVariables;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final String DRIVER = "org.postgresql.Driver";
    @Bean
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(DRIVER)
                .url(System.getProperty(EnvVariables.DB_URL.getValue()))
                .username(System.getProperty(EnvVariables.DB_USERNAME.getValue()))
                .password(System.getProperty(EnvVariables.DB_PASSWORD.getValue()))
                .build();
    }
}
