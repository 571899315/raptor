package com.raptor.registry;

import com.raptor.common.model.ServiceAddress;

public interface ServiceRegistry {
	void register(ServiceAddress serviceAddress);
}