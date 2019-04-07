package com.rpc.rpcx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ImportResource("classpath:spring/spring*.xml")
public class SpringBootProviderApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringBootProviderApplication.class);
    }
}
