package com.raptor.client;

import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.util.ExtensionLoader;
import com.raptor.loadbalancer.Cluster;
import com.raptor.loadbalancer.HaStrategy;
import com.raptor.loadbalancer.LoadBalance;
import com.raptor.proxy.ProxyFactory;
import com.raptor.registry.ServiceDiscovery;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * FactoryBean for service proxy
 *
 */
public  class ClientProxy implements FactoryBean<Object> {
	private Class<?> type;

	private ServiceDiscovery serviceDiscovery;

	private ClientConfig clientConfig;


	private Cluster cluster ;

	private ProxyFactory proxyFactory ;

	public ClientProxy(){}

	@Override
	public Object getObject() throws Exception {
		this.proxyFactory =  ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension(clientConfig.getProxy());
		this.cluster = ExtensionLoader.getExtensionLoader(Cluster.class).getExtension(clientConfig.getCluster());
		cluster.setHaStrategy(ExtensionLoader.getExtensionLoader(HaStrategy.class).getExtension(clientConfig.getHaStrategy()));
		LoadBalance lb = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(clientConfig.getLbStrategy());
		lb.setRegister(serviceDiscovery);
		cluster.setLoadBalance(lb);
		return proxyFactory.getProxy(type, this::invoke);
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


	public ClientProxy(Class<?> type, ServiceDiscovery serviceDiscovery, ClientConfig clientConfig) {
		this.type = type;
		this.serviceDiscovery = serviceDiscovery;
		this.clientConfig = clientConfig;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public ServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}
}
