package com.raptor.proxy;

import java.lang.reflect.InvocationHandler;

import com.raptor.common.annotation.SPI;

@SPI("jdk")
public interface ProxyFactory {

	<T> T getProxy(Class<T> clz, InvocationHandler invocationHandler);

}