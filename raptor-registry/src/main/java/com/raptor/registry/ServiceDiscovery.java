package com.raptor.registry;

import com.raptor.common.model.ServiceAddress;

import java.util.List;

public interface ServiceDiscovery {
	List<ServiceAddress> findAvailableAddresses(String interfaceName, String version);
}
