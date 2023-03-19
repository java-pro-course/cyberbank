package com.codemika.cyberbank.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {
    //    https://github.com/Netflix/eureka
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
