package com.raptor.loadbalancer.loadbalance;
import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.model.ServiceAddress;
import com.raptor.common.util.ExtensionLoader;
import com.raptor.loadbalancer.FailHost;
import com.raptor.loadbalancer.Invoker;
import com.raptor.loadbalancer.LoadBalance;
import com.raptor.registry.ServiceDiscovery;
import java.util.Iterator;
import java.util.List;



public abstract class AbstractLoadBalance implements LoadBalance {

	private ServiceDiscovery serviceDiscovery

	protected Invoker invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getDefaultExtension();

	@Override
	public RPCResponse invoke(RPCRequest request, ClientConfig config, List<FailHost> failHosts) throws Exception {
		List<ServiceAddress> addresses = serviceDiscovery.findAvailableAddresses(request.getInterfaceName(),request.getVersion());
		if (failHosts != null && failHosts.size() > 0) {
			Iterator<ServiceAddress> iterator = addresses.iterator();
			for (FailHost host : failHosts) {
				while (iterator.hasNext()) {
					ServiceAddress registerAddress = iterator.next();
					if (host.getIp().equals(registerAddress.getIp()) && host.getPort() == registerAddress.getPort()) {
						iterator.remove();
					}
				}
			}
		}
		if (addresses == null || addresses.size()==0) {
			throw new Exception("接口=" + request.getInterfaceName() + ",版本=" + request.getVersion());
		}
		invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getExtension(config.getInvoker());
		if (addresses.size() == 1) {
			request.setHost(addresses.get(0).getIp());
			request.setPort(addresses.get(0).getPort());
			return invoker.invoke(request, config);
		} else {
			return doInvoke(request, config, addresses);
		}
	}

	public abstract RPCResponse doInvoke(RPCRequest request, ClientConfig config, List<ServiceAddress> addresses)	throws Exception;

	@Override
	public void setRegister(ServiceDiscovery register) {
		this.serviceDiscovery = register;
	}

}
