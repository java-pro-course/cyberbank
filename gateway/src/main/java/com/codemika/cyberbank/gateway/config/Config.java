package com.codemika.cyberbank.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // аннотация для конфигурации конфиг-класса
public class Config {

    /**
     * Определение правил перемещения пользователя по ссылке
     *
     * @param builder
     * @return
     */
    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // todo: добавить новые микросервисы
        return builder.routes()
                .route("authentication", // id-сервиса
                        route -> route.path("/api/auth/**") // правило, по которому мы перемещаем пользователя
                                .uri("lb://authentication") // название урла
                ).build();
    }
}
