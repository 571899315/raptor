package com.raptor.sample.server;

import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ImportResource("classpath*:spring*.xml")
public class SpringBootServerApplication {

	 public static void main(String[] args) {
		 SpringApplication.run(SpringBootServerApplication.class);
	 }
}
