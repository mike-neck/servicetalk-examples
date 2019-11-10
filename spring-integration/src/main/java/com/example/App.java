/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.example;

import com.example.handlers.RunnerHandler;
import com.example.models.records.RecordRepository;
import com.example.models.runners.RunnerRepository;
import com.example.repositories.RecordRepositoryImpl;
import com.example.repositories.RunnerRepositoryImpl;
import io.servicetalk.data.jackson.JacksonSerializationProvider;
import io.servicetalk.http.api.HttpSerializationProvider;
import io.servicetalk.http.api.HttpSerializationProviders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootApplication
public class App implements ApplicationContextInitializer<GenericApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        logger.info("initializing application");
        applicationContext.registerBean(RunnerRepository.class, RunnerRepositoryImpl::new);
        applicationContext.registerBean(RecordRepository.class, RecordRepositoryImpl::new);
        applicationContext.registerBean(
                HttpSerializationProvider.class,
                () -> HttpSerializationProviders.jsonSerializer(new JacksonSerializationProvider()));
        applicationContext.registerBean(RunnerHandler.class);
        applicationContext.registerBean(ServiceTalkRunner.class);
        applicationContext.registerBean(
                CommandLineRunner.class,
                () -> applicationContext.getBean(ServiceTalkRunner.class));
    }
}