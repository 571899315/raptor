package com.raptor.common.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientConfig {

	private String contextPath = RaptorConstants.CONTEXT_PATH;

	private int connectTimeoutMillis = RaptorConstants.DEFAULT_connectTimeoutMillis;

	private int readTimeoutMillis = RaptorConstants.DEFAULT_readTimeoutMillis;

	private int retryCount = RaptorConstants.DEFAULT_RETRY_COUNT;

	private String lbStrategy = RaptorConstants.LB_RANDOM;

	private String haStrategy = RaptorConstants.HA_FAILFAST;

	private String threadpool = RaptorConstants.rpcx_threadpool;

	private String cluster = RaptorConstants.CLUSTER_TYPE;

	private String invoker = RaptorConstants.INVOKER_TYPE;


	private boolean sync = true;

	private boolean localFirst = false;

}
