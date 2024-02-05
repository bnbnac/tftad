package com.tftad;

import com.tftad.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class TftadApplication {

    public static void main(String[] args) {
        SpringApplication.run(TftadApplication.class, args);
    }

}
