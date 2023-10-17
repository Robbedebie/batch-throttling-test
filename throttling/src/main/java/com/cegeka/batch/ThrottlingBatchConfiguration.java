package com.cegeka.batch;

import com.cegeka.batch.throttling.DummyPerson;
import com.cegeka.batch.throttling.ThrottlingBatchItemReader;
import com.cegeka.batch.throttling.ThrottlingBatchPartitionerJobExecutionListener;
import com.cegeka.batch.throttling.ThrottlingBatchProcessor;
import com.cegeka.batch.throttling.ThrottlingChunckListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class ThrottlingBatchConfiguration {

    @Bean
    public Job job(ThrottlingBatchPartitionerJobExecutionListener jobExecutionListener,
                   PlatformTransactionManager transactionManager,
                   JobRepository jobRepository) {
        return new JobBuilder("job", jobRepository)
            .listener(jobExecutionListener)
            .start(stepProcessDummyPeople(transactionManager, jobRepository))
            .build();
    }

    @Bean
    ThrottlingBatchPartitionerJobExecutionListener jobExecutionListener() {
        return new ThrottlingBatchPartitionerJobExecutionListener();
    }

    @Bean
    public Step stepProcessDummyPeople(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        return new StepBuilder("stepProcessDummyPeople", jobRepository)
            .<DummyPerson, DummyPerson>chunk(10, transactionManager)
            .listener(getThrottlingChunckListener())
            .reader(getThrottlingBatchItemReader())
            .processor(getThrottlingBatchProcessor())
            .writer(itemWriter())
            .build();
    }

    @Bean
    public ThrottlingBatchItemReader getThrottlingBatchItemReader() {
        return new ThrottlingBatchItemReader();
    }

    @Bean
    public ThrottlingBatchProcessor getThrottlingBatchProcessor() {
        return new ThrottlingBatchProcessor();
    }

    @Bean
    public ThrottlingChunckListener getThrottlingChunckListener() {
        return new ThrottlingChunckListener();
    }

    @Bean
    public ItemWriter<DummyPerson> itemWriter() {
        return new ListItemWriter<>();
    }


}
