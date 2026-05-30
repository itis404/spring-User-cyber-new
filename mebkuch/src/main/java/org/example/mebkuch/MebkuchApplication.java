package org.example.mebkuch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
public class MebkuchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MebkuchApplication.class, args);
    }

}
