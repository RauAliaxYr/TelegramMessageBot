package org.di.dkdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
public class MainApplicationClass {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(MainApplicationClass.class,args);
    }
}
