package com.example.demo.config;


import com.example.demo.Bean.User;
import com.example.demo.task.JobCompletionNotificationListener;
import com.example.demo.task.UserItemProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.batch.item.database.orm.JpaQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;


import javax.persistence.EntityManagerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfig  {
    @Autowired
    public DataSource dataSource;
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    public Log log = LogFactory.getLog(this.getClass());
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public EntityManagerFactory entityManagerFactory;
   /* @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)

                .start(step1)
                .build();
    }*/



    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);

        factory.setMaxVarCharLength(1000);
        return factory.getObject();
    }




    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {

        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
               // .repository()
                .listener(listener)
                .start(step1)
                .build();
    }
    @Bean
    public Step step1(JdbcBatchItemWriter<User> writer) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(20);
        return stepBuilderFactory.get("step1")
                .<User, User> chunk(2000)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .taskExecutor(threadPoolTaskExecutor)
                .build();
    }



    @Bean
    public UserItemProcessor processor() {
        return new UserItemProcessor();
    }



 /*   @Bean
    public FlatFileItemReader<User> reader() {

        return new FlatFileItemReaderBuilder<User>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                    setTargetType(User.class);
                }})
                .build();
    }*/
/* @Bean
 public JdbcCursorItemReader<User> reader() {
     return new JdbcCursorItemReaderBuilder<User>()
             .dataSource(dataSource)
             .name("creditReader")
             .sql("select id, first_name, last_name from bc_user")
             .rowMapper(new UserRowMapper())
             .build();

 }*/
 @Bean
 public JpaPagingItemReader reader() {
     log.info(entityManagerFactory);
     return new JpaPagingItemReaderBuilder<User>()
             .name("creditReader")
             .entityManagerFactory(entityManagerFactory)
             //.queryString("select id, firstName, lastName from User")
             .queryProvider( queryProvider())
             .pageSize(100000)
             .build();
 }
    @Bean
    public JpaQueryProvider queryProvider() {

        JpaNativeQueryProvider<User> jpa = new JpaNativeQueryProvider<>();
           jpa.setEntityClass(User.class);
           jpa.setSqlQuery("select id, first_name, last_name from bc_user");
        return jpa;
    }


    @Bean
    public JdbcBatchItemWriter<User> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO bc_user_back (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }


}
