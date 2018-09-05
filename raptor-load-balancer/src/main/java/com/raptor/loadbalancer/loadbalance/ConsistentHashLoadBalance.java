package com.rpc.rpcx.cluster.loadbalance;

import java.util.List;

import com.rpc.rpcx.config.ConsumerConfig;
import com.rpc.rpcx.core.Request;
import com.rpc.rpcx.core.Response;
import com.rpc.rpcx.register.ProviderAddress;

public class ConsistentHashLoadBalance extends AbstractLoadBalance {

	@Override
	public Response doInvoke(Request request, ConsumerConfig config, List<ProviderAddress> addresses) throws Exception {
		return null;
	}

}
