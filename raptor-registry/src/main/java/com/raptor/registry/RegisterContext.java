package com.raptor.registry;

import com.raptor.common.model.ServiceAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public  class RegisterContext   {

	public static final ConcurrentMap<String, List<ServiceAddress>> REGISTER_PROVIDERS = new ConcurrentHashMap<String, List<ServiceAddress>>();

	public static void addServiceAddressToCache(ServiceAddress address) {
		List<ServiceAddress> addresses = REGISTER_PROVIDERS.get(address.getName() + "_" + address.getVersion());
		if (addresses == null) {
			addresses = new ArrayList<ServiceAddress>();
			addresses.add(address);
			REGISTER_PROVIDERS.put(address.getName() + "_" + address.getVersion(), addresses);
		} else {
			addresses.add(address);
		}
	}

	public static List<ServiceAddress> getAddressFromCache(String name, String version) {
		return REGISTER_PROVIDERS.get(name + "_" + version);
	}

	public static void deleteCacheValueByKey(String name, String version) {
		REGISTER_PROVIDERS.remove(name + "_" + version);
	}

	public static void putCache(String name, String version, List<ServiceAddress> addresses) {
		REGISTER_PROVIDERS.putIfAbsent(name + "_" + version, addresses);
	}

	public static Set<Map<String, String>> getCacheKeys() {
		Set<String> keys = REGISTER_PROVIDERS.keySet();
		Set<Map<String, String>> values = new HashSet<Map<String, String>>();
		for (String key : keys) {
			Map<String, String> nameVersion = new HashMap<String, String>();
			nameVersion.put("name", key.substring(0, key.indexOf("_")));
			nameVersion.put("version", key.substring(key.indexOf("_") + 1));
			values.add(nameVersion);
		}
		return values;
	}
}
