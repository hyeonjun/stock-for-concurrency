package com.example.stock.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories( // JPA Repository Bean 활성화
  basePackages = { // SpringBoot 가 Bean Scan 범위 설정 (Repository 패키지 설정)
    "com.example.stock.synchronize",
    "com.example.stock.pessimistic",
    "com.example.stock.optimistic"
  },
  // DataSource, Hibernate Property, Entity 설정 (Entity 패키지 설정)
  entityManagerFactoryRef = "mainEntityManager",
  // 스프링 트랜잭션 설정
  transactionManagerRef = "mainTransactionManager"
)
public class DatasourceConfig {

  @Primary
  @Bean
  public PlatformTransactionManager mainTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(mainEntityManager().getObject());
    return transactionManager;
  }

  // entityManagerFactoryRef 속성에 사용할 Bean 생성 메서드
  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean mainEntityManager() {
    // LocalContainerEntityManagerFactoryBean:
    // EntityManagerFactory를 생성하는 FactoryBean으로 DataSource,
    // Entity가 위치한 경로 Scan 설정, Hibernate 기반으로 동작하는 것을 설정하는
    // JpaVenoderAdapter 등을 지정합니다.
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(mainDataSource());
    em.setPackagesToScan(
      "com.example.stock.synchronize.domain",
      "com.example.stock.pessimistic.domain",
      "com.example.stock.optimistic.domain"
    );

    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
    em.setJpaVendorAdapter(adapter);
    return em;
  }

  @Primary
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public DataSource mainDataSource() {
    return DataSourceBuilder.create().build();
  }

}
