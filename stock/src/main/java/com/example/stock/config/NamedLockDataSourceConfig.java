package com.example.stock.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.example.stock.named",
  entityManagerFactoryRef = "namedLockEntityManager",
  transactionManagerRef = "namedLockTransactionManager"
)
public class NamedLockDataSourceConfig {

  @Bean
  public PlatformTransactionManager namedLockTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(namedLockEntityManager().getObject());
    return transactionManager;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean namedLockEntityManager() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(namedLockDataSource());
    em.setPackagesToScan("com.example.stock.named.domain");

    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
    adapter.setGenerateDdl(true);
    adapter.setShowSql(true);
    em.setJpaVendorAdapter(adapter);
    return em;
  }

  @Primary
  @Bean
  @ConfigurationProperties(prefix = "spring.named-lock-datasource.hikari")
  public DataSource namedLockDataSource() {
    return DataSourceBuilder.create().build();
  }



}
