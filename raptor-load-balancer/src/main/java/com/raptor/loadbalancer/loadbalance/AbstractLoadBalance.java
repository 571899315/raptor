package com.rpc.rpcx.cluster.loadbalance;

import java.util.Iterator;
import java.util.List;

import com.rpc.rpcx.cluster.FailHost;
import com.rpc.rpcx.cluster.LoadBalance;
import com.rpc.rpcx.config.ConsumerConfig;
import com.rpc.rpcx.core.Invoker;
import com.rpc.rpcx.core.Request;
import com.rpc.rpcx.core.Response;
import com.rpc.rpcx.core.extension.ExtensionLoader;
import com.rpc.rpcx.exception.NotFoundProviderException;
import com.rpc.rpcx.register.ProviderAddress;
import com.rpc.rpcx.register.Register;

public abstract class AbstractLoadBalance implements LoadBalance {

	protected Register register;

	protected Invoker invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getDefaultExtension();

	@Override
	public Response invoke(Request request, ConsumerConfig config, List<FailHost> failHosts) throws Exception {
		List<ProviderAddress> addresses = register.findAvailableAddresses(request.getInterfaceName(),
				request.getVersion());
		if (failHosts != null && failHosts.size() > 0) {
			Iterator<ProviderAddress> iterator = addresses.iterator();
			for (FailHost host : failHosts) {
				while (iterator.hasNext()) {
					ProviderAddress registerAddress = iterator.next();
					if (host.getIp().equals(registerAddress.getHost()) && host.getPort() == registerAddress.getPort()) {
						iterator.remove();
					}
				}
			}
		}
		if (addresses == null || addresses.size()==0) {
			throw new NotFoundProviderException("接口=" + request.getInterfaceName() + ",版本=" + request.getVersion());
		}
		invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getExtension(config.getInvoker());
		if (addresses.size() == 1) {
			request.setHost(addresses.get(0).getHost());
			request.setPort(addresses.get(0).getPort());
			return invoker.invoke(request, config);
		} else {
			return doInvoke(request, config, addresses);
		}
	}

	public abstract Response doInvoke(Request request, ConsumerConfig config, List<ProviderAddress> addresses)
			throws Exception;

	@Override
	public void setRegister(Register register) {
		this.register = register;
	}

}
