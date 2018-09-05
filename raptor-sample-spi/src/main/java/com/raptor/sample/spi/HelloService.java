package com.raptor.sample.spi;

import com.raptor.common.annotation.RaptorService;

@RaptorService(HelloService.class)
public interface HelloService {

	String hello(String name);
}
