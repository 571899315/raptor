package com.raptor.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.raptor.loadbalancer.Cluster;
import com.raptor.loadbalancer.HaStrategy;
import com.raptor.loadbalancer.LoadBalance;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

import com.raptor.client.ChannelManager;
import com.raptor.client.RPCResponseFuture;
import com.raptor.client.ResponseFutureManager;
import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.util.ExtensionLoader;
import com.raptor.proxy.ProxyFactory;
import com.raptor.registry.ServiceDiscovery;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * FactoryBean for service proxy
 *
 */
@Slf4j
@Data
public  class ClientProxy implements FactoryBean<Object> {
	private Class<?> type;

	private ServiceDiscovery serviceDiscovery;

	private ClientConfig clientConfig;


	//private Cluster cluster ;//= ExtensionLoader.getExtensionLoader(Cluster.class).getExtension(clientConfig.getCluster());;

	//private ProxyFactory proxyFactory ;

	public ClientProxy(){

		System.out.print("======");

//		ExtensionLoader<ProxyFactory> object  = ExtensionLoader.getExtensionLoader(ProxyFactory.class);
//
//		String proxy = clientConfig.getProxy();
//
//
//		Object ob = object.getExtension(clientConfig.getProxy());
//
//		System.out.print("======");
//
//		//Object call = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension(clientConfig.getProxy());
//		this.proxyFactory =  ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension(clientConfig.getProxy());
//		this.cluster = ExtensionLoader.getExtensionLoader(Cluster.class).getExtension(clientConfig.getCluster());
//		cluster.setHaStrategy(ExtensionLoader.getExtensionLoader(HaStrategy.class).getExtension(clientConfig.getHaStrategy()));
//		LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(clientConfig.getLbStrategy());
//		lb.setRegister(serviceDiscovery);
//		cluster.setLoadBalance(lb);
	}

	@Override
	public Object getObject() throws Exception {
		//return proxyFactory.getProxy(type, this);
		return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, this::invoke);
	}

	@Override
	public Class<?> getObjectType() {
		return this.type;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


//	private Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable {
//		String targetServiceName = type.getName();
//
//		// Create request
//		RPCRequest request = RPCRequest.builder().requestId(generateRequestId(targetServiceName)).interfaceName(method.getDeclaringClass().getName()).methodName(method.getName()).parameters(args).parameterTypes(method.getParameterTypes()).build();
//
//		// Get service address
//		InetSocketAddress serviceAddress = getServiceAddress(targetServiceName);
//
//		// Get channel by service address
//		Channel channel = ChannelManager.getInstance().getChannel(serviceAddress);
//		if (null == channel) {
//			throw new RuntimeException("Cann't get channel for address" + serviceAddress);
//		}
//
//		// Send request
//		RPCResponse response = sendRequest(channel, request);
//		if (response == null) {
//			throw new RuntimeException("response is null");
//		}
//		if (response.hasException()) {
//			throw response.getException();
//		} else {
//			return response.getResult();
//		}
//	}
//
	private String generateRequestId(String targetServiceName) {
		return targetServiceName + "-" + UUID.randomUUID().toString();
	}
//
//	private InetSocketAddress getServiceAddress(String targetServiceName) {
//		String serviceAddress = "";
//		if (serviceDiscovery != null) {
//			serviceAddress = serviceDiscovery.discover(targetServiceName);
//			log.debug("Get address: {} for service: {}", serviceAddress, targetServiceName);
//		}
//		if (StringUtils.isEmpty(serviceAddress)) {
//			throw new RuntimeException(String.format("Address of target service %s is empty", targetServiceName));
//		}
//		String[] array = StringUtils.split(serviceAddress, ":");
//		String host = array[0];
//		int port = Integer.parseInt(array[1]);
//		return new InetSocketAddress(host, port);
//	}
//
//	private RPCResponse sendRequest(Channel channel, RPCRequest request) {
//		CountDownLatch latch = new CountDownLatch(1);
//		RPCResponseFuture rpcResponseFuture = new RPCResponseFuture(request.getRequestId());
//		ResponseFutureManager.getInstance().registerFuture(rpcResponseFuture);
//		channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
//			latch.countDown();
//		});
//		try {
//			latch.await();
//		} catch (InterruptedException e) {
//			log.error(e.getMessage());
//		}
//
//		try {
//			// TODO: make timeout configurable
//			return rpcResponseFuture.get(1, TimeUnit.SECONDS);
//		} catch (Exception e) {
//			log.warn("Exception:", e);
//			return null;
//		}
//	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String targetServiceName = type.getName();
		// Create request
		RPCRequest request = new RPCRequest();//RPCRequest.builder().requestId(generateRequestId(targetServiceName)).interfaceName(method.getDeclaringClass().getName()).methodName(method.getName()).parameters(args).parameterTypes(method.getParameterTypes()).build();
//		RPCResponse response = cluster.invoke(request,clientConfig);
//		if(response.hasException()){
//			throw response.getException();
//		}
		return null;//response.getResult();
	}
}
