package com.example.stock.config.datasource;

import com.example.stock.config.datasource.DatasourceProperties.DatabasePlatform;
import com.example.stock.config.datasource.DatasourceProperties.DatabaseType;
import com.example.stock.config.datasource.DatasourceProperties.DdlOption;
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
    adapter.setDatabase(DatabaseType.MYSQL.getDatabase());

    CustomJpaProperties properties = new CustomJpaProperties();
    properties.setDdlAuto(DdlOption.CREATE);
    properties.setDatabasePlatform(DatabasePlatform.MYSQL8);

    em.setJpaVendorAdapter(adapter);
    em.setJpaProperties(properties.getJpaProperties());

    return em;
  }

  @Primary
  @Bean
  @ConfigurationProperties(prefix = "spring.named-lock-datasource.hikari")
  public DataSource namedLockDataSource() {
    return DataSourceBuilder.create().build();
  }
}
