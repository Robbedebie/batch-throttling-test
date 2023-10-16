package com.cegeka.batch.throttling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;


@Configuration
@EnableBatchProcessing
@ComponentScan
public class ThrottlingBatchConfiguration {

    public static Logger Log = LoggerFactory.getLogger(ThrottlingBatchConfiguration.class);

    public ThrottlingBatchConfiguration() {
        System.out.println("ThrottlingBatchConfiguration");
        Log.debug("DEBUG TEST______________________________________________________________________________________________________");
        Log.info("INFO TEST________________________________________________________________________________________________________");
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("TestJob", jobRepository)
            .start(stepProcessDummyPeople(jobRepository, transactionManager))
            .build();
    }

    @Bean
    public Step stepPartitioner(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("DummyPeopleProcessorStep", jobRepository)
            .partitioner("dummyPartitioner", throttlingBatchPartitioner())
            .step(stepProcessDummyPeople(jobRepository, transactionManager))
            .build();
    }

    @Bean
    public Step stepProcessDummyPeople(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("DummyPeopleProcessorStep", jobRepository).<DummyPerson, DummyPerson>chunk(10, transactionManager)
            .reader(getThrottlingBatchItemReader(null))
            .processor(throttlingBatchProcessor())
            .writer(itemWriter())
            .build();
    }

    @Bean
    public ThrottlingBatchPartitioner<DummyPerson> throttlingBatchPartitioner() {
        return new ThrottlingBatchPartitioner<>(1000);
    }

    @Bean
    @JobScope
    public ThrottlingBatchPartitionerJobExecutionListener throttlingBatchPartitionerJobExecutionListener() {
        return new ThrottlingBatchPartitionerJobExecutionListener();
    }

    @Bean
    @StepScope
    public ThrottlingBatchItemReader getThrottlingBatchItemReader(
        @Value("#{stepExecutionContext['partitionList']}") List<DummyPerson> dummyPeople) {
        return new ThrottlingBatchItemReader(dummyPeople);
    }

    @Bean
    @StepScope
    public ThrottlingBatchProcessor throttlingBatchProcessor() {
        return new ThrottlingBatchProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter<DummyPerson> itemWriter() {
        return new ListItemWriter<>();
    }
}
