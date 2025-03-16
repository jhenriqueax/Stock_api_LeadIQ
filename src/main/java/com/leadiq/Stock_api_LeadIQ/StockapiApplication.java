package com.leadiq.Stock_api_LeadIQ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class StockapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockapiApplication.class, args);
        System.out.println("Hello World");
    }
}
