package com.tftad;

import com.tftad.config.property.JwtProperty;
import com.tftad.config.property.GoogleOAuthProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({JwtProperty.class, GoogleOAuthProperty.class})
@SpringBootApplication
public class TftadApplication {

    public static void main(String[] args) {
        SpringApplication.run(TftadApplication.class, args);
    }

}
