package com.raptor.registry.impl.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.raptor.common.model.ServiceAddress;
import com.raptor.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class ConsulServiceDiscovery implements ServiceDiscovery {

	private ConsulClient consulClient;

	@Override
	public List<ServiceAddress> findAvailableAddresses(String interfaceName, String version) {
		return null;
	}
}
