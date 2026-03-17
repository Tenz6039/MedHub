package com.medhub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@Slf4j
public class MedHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedHubApplication.class, args);
        log.info("\n\n\n\n\n" + "START SERVER SUCCESS");
    }
}