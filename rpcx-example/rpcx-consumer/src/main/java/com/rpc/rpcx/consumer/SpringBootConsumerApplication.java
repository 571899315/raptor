package com.rpc.rpcx.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

import com.rpc.rpcx.UserRequest;
import com.rpc.rpcx.UserResponse;
import com.rpc.rpcx.UserService;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ImportResource("classpath:spring/spring*.xml")
public class SpringBootConsumerApplication {

//	@Autowired
//	private  UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(SpringBootConsumerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootConsumerApplication.class);
		ExecutorService service = Executors.newFixedThreadPool(3000);
		service.execute(new Runnable() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				for (int i = 0; i < 2000; i++) {
					for (int j = 0; j < 2000; j++) {
						UserRequest request = new UserRequest();
						UserService userService = SpringBeanFactory.getBean(UserService.class);
						UserResponse response = userService.getRequest(request);
						logger.info("result is:" + response);
					}
				}
				long end = System.currentTimeMillis();

				logger.info("result is:" + (end - start));

			}

		});

	}
}
