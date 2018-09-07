package com.raptor.protocol.client;

import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.loadbalancer.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractInvoker implements Invoker {

	private static final Logger logger = LoggerFactory.getLogger(AbstractInvoker.class);

//	private MetricsFactory metricsFactory = ExtensionLoader.getExtensionLoader(MetricsFactory.class)
//			.getDefaultExtension();

	@Override
	public RPCResponse invoke(RPCRequest request, ClientConfig config) throws Exception{
		RPCResponse response ;
		try {
			response = doInvoke(request,config);
		} catch (Exception e) {
			throw e;
		} finally {
		}
		return response;
	}

	public abstract RPCResponse doInvoke(RPCRequest request,  ClientConfig config) throws Exception;

}
