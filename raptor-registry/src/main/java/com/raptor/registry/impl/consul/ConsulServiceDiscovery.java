package com.raptor.registry.impl.consul;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.health.model.HealthService;
import com.raptor.common.model.ServiceAddress;
import com.raptor.registry.RegisterContext;
import com.raptor.registry.ServiceDiscovery;


public class ConsulServiceDiscovery implements ServiceDiscovery {

	private static final Logger log = LoggerFactory.getLogger(ConsulServiceDiscovery.class);



	private ConsulClient consulClient;

	private String host;
	private int port;

	public ConsulServiceDiscovery(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public List<ServiceAddress> findAvailableAddresses(String interfaceName, String version) {

		List<ServiceAddress> addresses = RegisterContext.getAddressFromCache(interfaceName, version);
		try {
			if (addresses == null || addresses.size() == 0) {
				addresses = findConsulAvailableAddresses(interfaceName, version);
				RegisterContext.putCache(interfaceName, version, addresses);
			}
		} catch (Exception e) {
			log.error("(name=" + interfaceName + ",version=" + version + ",error=" + e.getMessage() + ")");
		}
		return addresses;
	}


	public void init() {
		consulClient = new ConsulClient(host, port);
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.scheduleAtFixedRate(new ConsulRefresh(), 1, 50, TimeUnit.SECONDS);
	}

	public class ConsulRefresh implements Runnable {

		@Override
		public void run() {
			refresh();
		}
	}

	public synchronized void refresh() {
		long start = System.currentTimeMillis();
		Set<Map<String, String>> keys = RegisterContext.getCacheKeys();
		for (Map<String, String> key : keys) {
			try {
				String name = key.get("name");
				String version = key.get("version");
				List<ServiceAddress> availableProvider = findConsulAvailableAddresses(name, version);
				RegisterContext.deleteCacheValueByKey(name, version);
				RegisterContext.putCache(name, version, availableProvider);
			} catch (Exception e) {
				long stop = System.currentTimeMillis();
				log.error("error =" + stop + "time" + (stop - start) + ",error=" + e.getMessage() + ")");
			}
		}
		long stop = System.currentTimeMillis();
		log.debug("error =" + stop + "time" + (stop - start) + ")");
	}





	private List<ServiceAddress> findConsulAvailableAddresses(String name, String version) {
		com.ecwid.consul.v1.Response<List<HealthService>> services = consulClient.getHealthServices(name, version, true,
				QueryParams.DEFAULT);
		List<ServiceAddress> addresses = new ArrayList<ServiceAddress>();
		if (services != null && services.getValue() != null) {
			for (HealthService service : services.getValue()) {
				ServiceAddress address = new ServiceAddress();
				address.setIp(service.getService().getAddress());
				address.setPort(service.getService().getPort());
				address.setName(name);
				address.setVersion(version);
				addresses.add(address);
				RegisterContext.addServiceAddressToCache(address);
			}
		}
		RegisterContext.putCache(name, version, addresses);
		return addresses;
	}


}
