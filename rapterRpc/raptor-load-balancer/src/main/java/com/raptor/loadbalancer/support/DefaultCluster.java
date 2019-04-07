package com.raptor.loadbalancer.support;


import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.loadbalancer.Cluster;
import com.raptor.loadbalancer.HaStrategy;
import com.raptor.loadbalancer.LoadBalance;

public class DefaultCluster implements Cluster {

    private HaStrategy haStrategy;

    private LoadBalance loadBalance;


	@Override
	public RPCResponse invoke(RPCRequest request, ClientConfig config) throws Exception {
		return haStrategy.invoke(request,config,loadBalance);
	}

	public HaStrategy getHaStrategy() {
		return haStrategy;
	}

	public void setHaStrategy(HaStrategy haStrategy) {
		this.haStrategy = haStrategy;
	}

	public LoadBalance getLoadBalance() {
		return loadBalance;
	}

	public void setLoadBalance(LoadBalance loadBalance) {
		this.loadBalance = loadBalance;
	}
}