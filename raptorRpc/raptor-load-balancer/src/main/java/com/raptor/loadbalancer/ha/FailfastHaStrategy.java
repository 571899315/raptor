package com.raptor.loadbalancer.ha;


import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.loadbalancer.HaStrategy;
import com.raptor.loadbalancer.LoadBalance;

public class FailfastHaStrategy implements HaStrategy {
	@Override
	public RPCResponse invoke(RPCRequest request, ClientConfig config, LoadBalance loadBalance) throws Exception {
		return loadBalance.invoke(request, config,null);
	}
}