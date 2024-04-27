package com.example.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Redis 동시성 처리를 위한 Transaction
 * Spring Data Redis에서 Transaction 을 사용할 수 있는 두 가지 방법
 * 1. SessionCallback 인터페이스를 통해 여러 명령을 하나로 묶어서 처리하는 방법
 *    이 방법은 인터페이스를 통해 직접적으로 Redis 명령어를 사용하여 트랜잭션 경계를 설정하는 방법
 * 2. @Transactional 어노테이션을 사용하는 방법
 *    이는 PlatformTransactionManager 를 Bean 으로 등록하는 과정이 필요
 *    PlatformTransactionManager 구현체를 제공하지 않기 때문에
 *    JDBC 의 DataSourceTransactionManager 를 사용하거나 JPA 의 JpaTransactionManager 를 사용해야 한다.
 *    이를 사용하기 위해 @EnableTransactionManagement 적용이 필요하고
 *    redisTemplate 의 EnableTransactionSupport 값을 true 로 설정해야 한다.
 */
@Configuration
@EnableTransactionManagement
public class RedisConfig {

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory();
  }

  @Bean
  public RedisTemplate<?, ?> redisTemplate() {
    RedisTemplate<?, ?> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory());
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.setEnableTransactionSupport(true); // 설정 필요
    return template;
  }

  /* JPA 의 JpaTransactionManager 를 사용하는 방법 */

  @Bean
  public PlatformTransactionManager transactionManager() {
    return new JpaTransactionManager();
  }

  /* JDBC 의 DataSourceTransactionManager 를 사용하는 방법
  @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
   */

}
