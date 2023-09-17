package com.example.campusin;
/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // https://europani.github.io/spring/2021/10/05/027-baseTimeEntity.html
@SpringBootApplication
public class CampusinApplication {
    public static void main(String[] args) {

        SpringApplication.run(CampusinApplication.class, args);
    }

}
