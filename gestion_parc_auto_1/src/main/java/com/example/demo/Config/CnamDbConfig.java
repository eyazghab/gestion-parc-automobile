package com.example.demo.Config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.demo.RepositoryCnam", // repos CNAM
    entityManagerFactoryRef = "cnamEntityManagerFactory",
    transactionManagerRef = "cnamTransactionManager"
)
public class CnamDbConfig {

    @Bean(name = "cnamDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.cnam")
    public DataSource cnamDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "cnamEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean cnamEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("cnamDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.demo.cnam") // entités cnam_db
                .persistenceUnit("cnam")
                .build();
    }

    @Bean(name = "cnamTransactionManager")
    public PlatformTransactionManager cnamTransactionManager(
            @Qualifier("cnamEntityManagerFactory") EntityManagerFactory cnamEntityManagerFactory) {
        return new JpaTransactionManager(cnamEntityManagerFactory);
    }
}
