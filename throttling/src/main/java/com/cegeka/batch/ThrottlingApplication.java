package com.cegeka.batch;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ThrottlingApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ThrottlingApplication.class, args);

        Environment environment = run.getEnvironment();

        String property = environment.getProperty("spring.batch.jdbc.initialize-schema");

        JobLauncher jobLauncher = run.getBean(JobLauncher.class);
        Job job = run.getBean(Job.class);
        JobParameters jobParameters = new JobParametersBuilder().toJobParameters();

        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }

}
