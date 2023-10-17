package com.cegeka.batch;

import com.cegeka.batch.throttling.DummyPerson;
import com.cegeka.batch.throttling.ThrottlingBatchItemReader;
import com.cegeka.batch.throttling.ThrottlingBatchPartitionerJobExecutionListener;
import com.cegeka.batch.throttling.ThrottlingBatchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.batch.support.DatabaseType;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Configuration
@EnableAutoConfiguration
public class ThrottlingBatchConfiguration {

    public static Logger Log = LoggerFactory.getLogger(ThrottlingBatchConfiguration.class);

    public ThrottlingBatchConfiguration() {
        System.out.println("ThrottlingBatchConfiguration");
        Log.debug("DEBUG TEST______________________________________________________________________________________________________");
        Log.info("INFO TEST________________________________________________________________________________________________________");
    }


    @Bean
    public ResourcelessTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDatabaseType(DatabaseType.H2.getProductName());
        factory.setDataSource(dataSource());
        factory.setTransactionManager(transactionManager());
        return factory.getObject();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:test");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public Job job(Step stepProcessDummyPeople, ThrottlingBatchPartitionerJobExecutionListener jobExecutionListener) throws Exception {
        return new JobBuilder("job",jobRepository())
            .incrementer(new RunIdIncrementer())
            .listener(jobExecutionListener)
            .start(stepProcessDummyPeople)
            .build();
    }

    @Bean
    ThrottlingBatchPartitionerJobExecutionListener jobExecutionListener() {
        return new ThrottlingBatchPartitionerJobExecutionListener();
    }

    @Bean
    public Step stepProcessDummyPeople() throws Exception {
        return new StepBuilder("stepProcessDummyPeople",jobRepository())
            .<DummyPerson, DummyPerson>chunk(10,transactionManager())
            .reader(getThrottlingBatchItemReader())
            .writer(itemWriter())
            .build();
    }


    @Bean
    public ThrottlingBatchItemReader getThrottlingBatchItemReader() {
        return new ThrottlingBatchItemReader();
    }

    @Bean
    public ThrottlingBatchProcessor throttlingBatchProcessor() {
        return new ThrottlingBatchProcessor();
    }

    @Bean
    public ItemWriter<DummyPerson> itemWriter() {
        return new ListItemWriter<>();
    }


}
