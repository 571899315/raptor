package com.raptor.registry.impl.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.agent.model.NewService;
import com.raptor.common.model.ServiceAddress;
import com.raptor.common.util.StringUtils;
import com.raptor.registry.RegisterContext;
import com.raptor.registry.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

public class ConsulServiceRegistry implements ServiceRegistry, InitializingBean {

	private ConsulClient consulClient;

	private String consulAddress;

	@Override
	public void afterPropertiesSet() throws Exception {

		if (StringUtils.isBlank(consulAddress)) {
			return;
		}
		String address[] = consulAddress.split(":");
		ConsulRawClient rawClient = new ConsulRawClient(address[0], Integer.valueOf(address[1]));
		consulClient = new ConsulClient(rawClient);

	}

	@Override
	public void register(ServiceAddress serviceAddress) {
		NewService newService = new NewService();
		newService.setId(generateNewIdForService(serviceAddress.getName(), serviceAddress));
		newService.setName(serviceAddress.getName());
		List<String> version = new ArrayList<String>();
		version.add(serviceAddress.getVersion());
		newService.setTags(version);
		newService.setAddress(serviceAddress.getIp());
		newService.setPort(serviceAddress.getPort());
		NewService.Check check = new NewService.Check();
		check.setTcp(serviceAddress.toString());
		check.setInterval("1s");
		newService.setCheck(check);
		consulClient.agentServiceRegister(newService);
		RegisterContext.addServiceAddressToCache(serviceAddress);
	}

	private String generateNewIdForService(String serviceName, ServiceAddress serviceAddress) {
		// serviceName + ip + port
		return serviceName + "-" + serviceAddress.getIp() + "-" + serviceAddress.getPort();
	}

	public String getConsulAddress() {
		return consulAddress;
	}

	public void setConsulAddress(String consulAddress) {
		this.consulAddress = consulAddress;
	}

}
