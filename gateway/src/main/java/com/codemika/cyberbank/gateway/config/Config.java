package com.codemika.cyberbank.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // с помощью этой аннотации мы управляем gateway (конфигурируем его)
public class Config {

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // TODO: изучить лямбды
        return builder.routes()
                .route("authentication", // id-сервиса
                        route -> route.path("/api/auth/**") // правило, по которому мы перемещаем пользователя
                                .uri("lb://authentication") // название урла
                ).build();
    }
}
