package com.tftad.config;

import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.config.property.JwtProperty;
import com.tftad.config.resolver.AuthResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtProperty jwtProperty;
    private final GoogleOAuthProperty googleOAuthProperty;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthResolver(jwtProperty, googleOAuthProperty));
    }
}
