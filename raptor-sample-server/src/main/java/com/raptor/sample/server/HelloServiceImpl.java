package com.raptor.sample.server;

import com.raptor.sample.spi.HelloService;
import org.springframework.stereotype.Component;

@Component
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		System.out.println("HelloService come");
		return "Hello! " + name;
	}

}
