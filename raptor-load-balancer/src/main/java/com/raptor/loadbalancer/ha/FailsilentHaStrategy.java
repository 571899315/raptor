package com.rpc.rpcx.cluster.ha;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rpc.rpcx.cluster.FailHost;
import com.rpc.rpcx.cluster.HaStrategy;
import com.rpc.rpcx.cluster.LoadBalance;
import com.rpc.rpcx.config.ConsumerConfig;
import com.rpc.rpcx.core.Request;
import com.rpc.rpcx.core.Response;
import com.rpc.rpcx.exception.ConnectException;
import com.rpc.rpcx.exception.ReadException;

public class FailsilentHaStrategy implements HaStrategy {

	public static final Logger logger = LoggerFactory.getLogger(FailsilentHaStrategy.class);

	@Override
	public Response invoke(Request request, ConsumerConfig config, LoadBalance loadBalance) throws Exception {
		try {

			int tryCount = 1;
			if (config.getRetryCount() > 1) {
				tryCount = config.getRetryCount();
			}
			List<FailHost> failHosts = new ArrayList<FailHost>();
			for (int i = 0; i < tryCount; i++) {
				try {
					return loadBalance.invoke(request, config, failHosts);
				} catch (ReadException | ConnectException e) {
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
			Object obj = config.getMock();
			if (obj == null) {
				logger.info("failsilent return null");
				return null;
			}

			try {
				Class<?> clazz = Class.forName(request.getInterfaceName());
				Method method = clazz.getMethod(request.getMethod(), request.getParamTypes());
				Object result = method.invoke(obj, request.getParamValues());
				Response response = new Response();
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