package com.raptor.sample.spi;

import com.raptor.common.annotation.RPCService;

@RPCService(HelloService.class)
public interface HelloService {

	String hello(String name);
}
