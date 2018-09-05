package com.raptor.sample.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.raptor.sample.spi.HelloService;

@Component
@Slf4j
public class AnotherService {
	@Autowired
	private HelloService helloService;

	public void callHelloService() {
		System.out.print("call hello service");
		System.out.print("Result of callHelloService: {}"+helloService.hello("world"));
	}
}
