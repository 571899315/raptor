package com.raptor.registry;

import com.raptor.common.model.ServiceAddress;

public interface ServiceRegistry {
	void register(String serviceName, ServiceAddress serviceAddress);
}