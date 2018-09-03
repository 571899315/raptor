package com.raptor.proxy.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.raptor.extension.Adaptive;
import com.raptor.proxy.ProxyFactory;

@Adaptive
public class JdkProxyFactory implements ProxyFactory{
	
	@Override
	 public <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {clz}, invocationHandler);
    }
}

