package com.raptor.loadbalancer.loadbalance;


import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.model.ServiceAddress;

import java.util.List;

public class ConsistentHashLoadBalance extends AbstractLoadBalance {

	@Override
	public RPCResponse doInvoke(RPCRequest request, ClientConfig config, List<ServiceAddress> addresses){
		return null;
	}

}
