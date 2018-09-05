package com.rpc.rpcx.cluster.loadbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rpc.rpcx.config.ConsumerConfig;
import com.rpc.rpcx.core.Request;
import com.rpc.rpcx.core.Response;
import com.rpc.rpcx.core.extension.ExtensionLoader;
import com.rpc.rpcx.metrics.MetricsFactory;
import com.rpc.rpcx.register.ProviderAddress;

public class RandomLoadBalance extends AbstractLoadBalance {

	private static Random random = new Random();

	private MetricsFactory metricsFactory = ExtensionLoader.getExtensionLoader(MetricsFactory.class)
			.getDefaultExtension();

	@Override
	public Response doInvoke(Request request, ConsumerConfig config, List<ProviderAddress> addresses) throws Exception {
		ProviderAddress address = doSelect(addresses);
		request.setHost(address.getHost());
		request.setPort(address.getPort());
		return invoker.invoke(request, config);
	}

	private ProviderAddress doSelect(List<ProviderAddress> addresses) {
		double maxAvgTime = 0;
		for (ProviderAddress address : addresses) {
			double callTime75thPercentile = metricsFactory.getCallTime(address.getHost(), address.getPort())
					.getSnapshot().get75thPercentile();
			if (callTime75thPercentile > maxAvgTime) {
				maxAvgTime = metricsFactory.getCallTime(address.getHost(), address.getPort()).getSnapshot()
						.get75thPercentile();
			}
		}
		List<HostWeight> hostWeights = new ArrayList<HostWeight>();
		for (ProviderAddress address : addresses) {
			HostWeight weight = new HostWeight();
			weight.setAddress(address);
			double callTime75thPercentile = metricsFactory.getCallTime(address.getHost(), address.getPort())
					.getSnapshot().get75thPercentile();
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

	public class HostWeight {
		private Integer weight;
		private ProviderAddress address;

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		public ProviderAddress getAddress() {
			return address;
		}

		public void setAddress(ProviderAddress address) {
			this.address = address;
		}
	}
}