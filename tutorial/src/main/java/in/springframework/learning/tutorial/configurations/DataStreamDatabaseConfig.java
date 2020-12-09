package in.springframework.learning.tutorial.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "dsEntityManagerFactory",
        transactionManagerRef = "dsTransactionManager",
        basePackages = "in.springframework.learning.tutorial.ds.repositories")
public class DataStreamDatabaseConfig {
    @Value("${ds.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${ds.datasource.url}")
    private String url;
    @Value("${ds.datasource.username}")
    private String username;
    @Value("${ds.datasource.password}")
    private String password;
    @Value("${ds.jpa.hibernate.ddl-auto}")
    private String ddlAuto;
    @Value("${ds.jpa.properties.hibernate.dialect}")
    private String dialect;
    @Value("${ds.jpa.show-sql}")
    private String showSql;
    @Value("${ds.jpa.properties.hibernate.format_sql}")
    private String formatSql;

    @Bean(name = "dsDataSource")
    @ConfigurationProperties(prefix = "ds.datasource")
    public DataSource dsDataSource() {
        return DataSourceBuilder
                .create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
    @Bean("dsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(EntityManagerFactoryBuilder builder,
                         @Qualifier("dsDataSource") DataSource dataSource) {

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl-auto", ddlAuto);
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.show-sql", showSql);
        properties.setProperty("hibernate.format_sql", formatSql);
        LocalContainerEntityManagerFactoryBean bean
                = builder
                .dataSource(dataSource)
                .packages("in.springframework.learning.tutorial.ds.entities")
                .persistenceUnit("dsPU")
                .build();
        bean.setJpaProperties(properties);
        return bean;
    }
    @Bean(name = "dsTransactionManager")
    public PlatformTransactionManager
    transactionManager(@Qualifier("dsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
