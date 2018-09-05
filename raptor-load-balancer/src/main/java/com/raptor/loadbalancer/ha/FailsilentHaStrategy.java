package com.raptor.loadbalancer.ha;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.loadbalancer.FailHost;
import com.raptor.loadbalancer.HaStrategy;
import com.raptor.loadbalancer.LoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FailsilentHaStrategy implements HaStrategy {

	public static final Logger logger = LoggerFactory.getLogger(FailsilentHaStrategy.class);

	@Override
	public RPCResponse invoke(RPCRequest request, ClientConfig config, LoadBalance loadBalance) throws Exception {
		try {

			int tryCount = 1;
			if (config.getRetryCount() > 1) {
				tryCount = config.getRetryCount();
			}
			List<FailHost> failHosts = new ArrayList<FailHost>();
			for (int i = 0; i < tryCount; i++) {
				try {
					return loadBalance.invoke(request, config, failHosts);
				} catch (Exception  e) {
					FailHost host = new FailHost();
					host.setIp(request.getHost());
					host.setPort(request.getPort());
					failHosts.add(host);
					logger.error(e.getMessage(), e);
					if (i == (tryCount - 1)) {
						throw e;
					}
				}
			}
			return null;
		} catch (Exception e) {
//			Object obj = config.getMock();
//			if (obj == null) {
//				logger.info("failsilent return null");
//				return null;
//			}

			try {
				Class<?> clazz = Class.forName(request.getInterfaceName());
				Method method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
				Object result = method.invoke(null, request.getParameters());
				RPCResponse response = new RPCResponse();
				response.setResult(result);
				logger.info("failmock return {}", Objects.toString(result));
				return response;
			} catch (Exception e_1) {
				logger.error("mock fail return null", e_1);
				return null;
			}

		}
	}

}