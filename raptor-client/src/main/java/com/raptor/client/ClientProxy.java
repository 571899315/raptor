package com.raptor.client;

import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
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


//
	private String generateRequestId(String targetServiceName) {
		return targetServiceName + "-" + UUID.randomUUID().toString();
	}


	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String targetServiceName = type.getName();
		RPCRequest request = new RPCRequest();
        request.setRequestId(generateRequestId(targetServiceName));
        request.setInterfaceName(method.getDeclaringClass().getName());
		request.setVersion(clientConfig.getVersion());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
		RPCResponse response = cluster.invoke(request,clientConfig);
		if(response.hasException()){
			throw response.getException();
		}
		return response;
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
