package com.raptor.loadbalancer.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.model.ServiceAddress;


public class RandomLoadBalance extends AbstractLoadBalance {

	private static Random random = new Random();

//	private MetricsFactory metricsFactory = ExtensionLoader.getExtensionLoader(MetricsFactory.class)
//			.getDefaultExtension();

	@Override
	public RPCResponse doInvoke(RPCRequest request, ClientConfig config, List<ServiceAddress> addresses)throws Exception {
		ServiceAddress address = doSelect(addresses);
		request.setHost(address.getIp());
		request.setPort(address.getPort());
		return invoker.invoke(request, config);
	}

	private ServiceAddress doSelect(List<ServiceAddress> addresses) {
		double maxAvgTime = 0;
//		for (ServiceAddress address : addresses) {
//			double callTime75thPercentile = metricsFactory.getCallTime(address.getHost(), address.getPort())
//					.getSnapshot().get75thPercentile();
//			if (callTime75thPercentile > maxAvgTime) {
//				maxAvgTime = metricsFactory.getCallTime(address.getHost(), address.getPort()).getSnapshot()
//						.get75thPercentile();
//			}
//		}
		List<HostWeight> hostWeights = new ArrayList<HostWeight>();
		for (ServiceAddress address : addresses) {
			HostWeight weight = new HostWeight();
			weight.setAddress(address);
			double callTime75thPercentile = 0;//metricsFactory.getCallTime(address.getHost(), address.getPort()).getSnapshot().get75thPercentile();
			if (callTime75thPercentile == 0) {
				weight.setWeight(10);
			} else {
				weight.setWeight((int)(maxAvgTime -  callTime75thPercentile) + 10);
			}
			hostWeights.add(weight);
		}
		int weightSum = 0;
		for (HostWeight wc : hostWeights) {
			weightSum += wc.getWeight();
		}
		Integer n = random.nextInt(weightSum);
		Integer m = 0;
		for (HostWeight wc : hostWeights) {
			if (m <= n && n < m + wc.getWeight()) {
				return wc.getAddress();
			}
			m += wc.getWeight();
		}
		return addresses.get(random.nextInt(addresses.size()));
	}

	 class HostWeight {
		private Integer weight;
		private ServiceAddress address;

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		public ServiceAddress getAddress() {
			return address;
		}

		public void setAddress(ServiceAddress address) {
			this.address = address;
		}
	}
}
