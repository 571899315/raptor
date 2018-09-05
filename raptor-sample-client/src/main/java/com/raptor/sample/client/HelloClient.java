package com.raptor.sample.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloClient {

	public static void main(String[] args) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//		AnotherService anotherService = context.getBean(AnotherService.class);
//		anotherService.callHelloService();

	}
}
