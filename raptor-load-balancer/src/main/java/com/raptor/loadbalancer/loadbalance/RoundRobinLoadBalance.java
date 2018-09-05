package com.rpc.rpcx.cluster.loadbalance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.rpc.rpcx.config.ConsumerConfig;
import com.rpc.rpcx.core.Request;
import com.rpc.rpcx.core.Response;
import com.rpc.rpcx.register.ProviderAddress;

public class RoundRobinLoadBalance extends AbstractLoadBalance {

	private static final ConcurrentMap<String, AtomicInteger> InterfaceNameServerCyclicCounter = new ConcurrentHashMap<String, AtomicInteger>();

	private int incrementAndGetModulo(Request request, int modulo) {
		AtomicInteger nextServerCyclicCounter = InterfaceNameServerCyclicCounter
				.get(request.getInterfaceName() + "_" + request.getVersion());
		if (nextServerCyclicCounter == null) {
			nextServerCyclicCounter = new AtomicInteger(0);
		}
		for (;;) {
			int current = nextServerCyclicCounter.get();
			int next = (current + 1) % modulo;
			if (nextServerCyclicCounter.compareAndSet(current, next)) {
				InterfaceNameServerCyclicCounter.putIfAbsent(request.getInterfaceName() + "_" + request.getVersion(),
						nextServerCyclicCounter);
				return next;
			}
		}
	}

	@Override
	public Response doInvoke(Request request, ConsumerConfig config, List<ProviderAddress> addresses) throws Exception {
		ProviderAddress address = null;
		int upCount = addresses.size();
		int nextServerIndex = incrementAndGetModulo(request, upCount);
		address = addresses.get(nextServerIndex);
		request.setHost(address.getHost());
		request.setPort(address.getPort());
		return invoker.invoke(request, config);
	}
}