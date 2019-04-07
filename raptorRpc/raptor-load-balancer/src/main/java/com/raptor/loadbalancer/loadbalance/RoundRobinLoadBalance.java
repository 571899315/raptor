package com.raptor.loadbalancer.loadbalance;

import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.model.ServiceAddress;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;



public class RoundRobinLoadBalance extends AbstractLoadBalance {

	private static final ConcurrentMap<String, AtomicInteger> InterfaceNameServerCyclicCounter = new ConcurrentHashMap<String, AtomicInteger>();

	private int incrementAndGetModulo(RPCRequest request, int modulo) {
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
	public RPCResponse doInvoke(RPCRequest request, ClientConfig config, List<ServiceAddress> addresses)throws Exception {
		ServiceAddress address = null;
		int upCount = addresses.size();
		int nextServerIndex = incrementAndGetModulo(request, upCount);
		address = addresses.get(nextServerIndex);
		request.setHost(address.getIp());
		request.setPort(address.getPort());
		return invoker.invoke(request, config);
	}
}