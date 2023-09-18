package com.example.campusin.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by kok8454@gmail.com on 2023-09-18
 * Github : http://github.com/perArdua
 */
@Component
public class DataSourcePrinter implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DATASOURCE : " + datasourceUrl);
    }
}
