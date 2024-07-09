package com.tftad;

import com.tftad.config.property.AuthProperty;
import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.config.property.JwtProperty;
import com.tftad.config.property.Urls;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        JwtProperty.class,
        GoogleOAuthProperty.class,
        Urls.class,
        AuthProperty.class
})
@SpringBootApplication
public class TftadApplication {
    data.sql 뜨는지
    확인 안뜨게합시다

    public static void main(String[] args) {
        SpringApplication.run(TftadApplication.class, args);
    }

}
