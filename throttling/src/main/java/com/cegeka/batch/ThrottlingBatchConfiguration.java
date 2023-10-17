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
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class ThrottlingBatchConfiguration {
    public static Logger Log = LoggerFactory.getLogger(ThrottlingBatchConfiguration.class);

    public ThrottlingBatchConfiguration() {
        System.out.println("ThrottlingBatchConfiguration");
        Log.debug("DEBUG TEST______________________________________________________________________________________________________");
        Log.info("INFO TEST________________________________________________________________________________________________________");
    }


    @Bean
    public Job job(ThrottlingBatchPartitionerJobExecutionListener jobExecutionListener, PlatformTransactionManager transactionManager, JobRepository jobRepository) throws Exception {
        return new JobBuilder("job", jobRepository)
            .listener(jobExecutionListener)
            .start(stepProcessDummyPeople(transactionManager,jobRepository))
            .build();
    }

    @Bean
    ThrottlingBatchPartitionerJobExecutionListener jobExecutionListener() {
        return new ThrottlingBatchPartitionerJobExecutionListener();
    }

    @Bean
    public Step stepProcessDummyPeople(PlatformTransactionManager transactionManager, JobRepository jobRepository) throws Exception {
        return new StepBuilder("stepProcessDummyPeople", jobRepository)
            .<DummyPerson, DummyPerson>chunk(10, transactionManager)
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
