package com.nagesoft.club.infra.config;

import com.nagesoft.club.modules.notification.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;
//    private final EnrollmentInterceptor enrollmentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> staticResourcesPath = Arrays.stream(StaticResourceLocation.values())
                        .flatMap(StaticResourceLocation::getPatterns)
                                .collect(Collectors.toList());
        staticResourcesPath.add("/node_modules/**");

        registry.addInterceptor(notificationInterceptor)
//                .addInterceptor(enrollmentInterceptor)
                .excludePathPatterns(staticResourcesPath);
    }
}
