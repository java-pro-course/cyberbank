package com.codemika.cyberbank.gateway.config;

//import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;

@Configuration // аннотация для конфигурации конфиг-класса
public class Config {

    /**
     * Определение правил перемещения пользователя по ссылке
     * @param builder
     * @return
     */
    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        //TODO: изучить лямбды
        return builder.routes()
                .route("authentication", // id-сервиса
                        route -> route.path("/api/auth/**") // правило, по которому мы перемещаем пользователя
                                .uri("lb://authentication") // название урла
                ).build();
    }
//
//    @Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        return builder.build();
//    }
}
