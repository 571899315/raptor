package com.raptor.registry;

import com.raptor.common.model.ServiceAddress;

import java.util.List;

/**
 * @author hongbin
 * Created on 21/10/2017
 */
public interface ServiceDiscovery {
	String discover(String serviceName);
	List<ServiceAddress> findAvailableAddresses(String interfaceName, String version);
}
