package com.raptor.loadbalancer.ha;



import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.loadbalancer.FailHost;
import com.raptor.loadbalancer.HaStrategy;
import com.raptor.loadbalancer.LoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class FailoverHaStrategy implements HaStrategy {
	
	private static final Logger logger = LoggerFactory.getLogger(FailoverHaStrategy.class);

	@Override
	public RPCResponse invoke(RPCRequest request, ClientConfig config, LoadBalance loadBalance) throws Exception {
		int tryCount = 1;
		if (config.getRetryCount()>1) {
			tryCount = config.getRetryCount();
		}
		List<FailHost> failHosts = new ArrayList<FailHost>();
		for(int i=0;i<tryCount;i++) {
			try {
				return loadBalance.invoke(request, config,failHosts);
			} catch (Exception e) {
				FailHost host = new FailHost();
				host.setIp(request.getHost());
				host.setPort(request.getPort());
				failHosts.add(host);
				logger.error(e.getMessage(),e);
				if (i == (tryCount-1)) {
					throw e;
				}
			}
		}
		return null;
	}
}