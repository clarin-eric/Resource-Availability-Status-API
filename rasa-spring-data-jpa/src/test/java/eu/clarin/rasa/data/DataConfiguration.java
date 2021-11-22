package eu.clarin.rasa.data;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("eu.clarin.rasa.data.repositories")
@EnableTransactionManagement
@ComponentScan
public class DataConfiguration {
   
   @Bean
   public DataSource dataSource() {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
      dataSource.setUrl("jdbc:mariadb://localhost:3306/linkchecker_test");
      dataSource.setUsername( "testuser" );
      dataSource.setPassword( "testuser" );
      return dataSource;
   }
   
   @Bean
   public EntityManagerFactory entityManagerFactory() {
      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      vendorAdapter.setGenerateDdl(true);
      
      Properties jpaProperties = new Properties();
      jpaProperties.put("hibernate.hbm2ddl.auto", "none");
      jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MariaDB103Dialect");
      
      LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
      factory.setDataSource(dataSource());
      factory.setPackagesToScan("eu.clarin.rasa.data.entities");
      factory.setJpaVendorAdapter(vendorAdapter);
      factory.setJpaProperties(jpaProperties);
      factory.afterPropertiesSet();
      
      return factory.getObject();
   }
   
   @Bean
   PlatformTransactionManager transactionManager() {
      JpaTransactionManager txManager = new JpaTransactionManager();
      txManager.setEntityManagerFactory(entityManagerFactory());
      
      return txManager;
   }

}
