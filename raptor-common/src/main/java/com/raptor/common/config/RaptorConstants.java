package com.raptor.common.config;

public class RaptorConstants {
	
	public static final String LASTEST_VERSION="Lastest";
	
	public static final String CONTEXT_PATH="/";
	
	public static final int DEFAULT_RETRY_COUNT=3;
	
	public static final int DEFAULT_connectTimeoutMillis=3000;
	
	public static final int DEFAULT_readTimeoutMillis=5000;
	
	public static final int DEFAULT_healthInterval=2;
	
	public static final int DEFAULT_refreshInterval=3;
	
	public static final String LB_RANDOM = "random";
	
	public static final String HA_FAILFAST = "failfast";
	
	public static final String HA_FAILOVER = "failover";

	public static final String HA_FAILSILENT = "failsilent";
	
	public static final String rpcx_ROOT_PATH = "rpcx-service";
	
	public static final String HOST = "HOST";
	
	public static final String PORT = "PORT";
	
	public static final String rpcx_threadpool = "fixed";
	
	public static final int rpcx_threads = 100;
	
	public static final int rpcx_queues = 0;
	
	public static final int rpcx_cores = 0;
	
	public static final int rpcx_alive = 60 * 1000;
	
	public static final String DEFAULT_THREAD_NAME = "rpcx";
	
	public static final String CLUSTER_TYPE= "default";
	
	public static final String INVOKER_TYPE="default";
	
	public static final String Serialization_TYPE="protostuff";

	public static final String DEFAULT_TRUST_STORE = "META-INF/truststore.jks";

	public static final String DEFAULT_TRUST_PWD = "123456";
	
	public static final String DefaultMetricsFactory = "default";

	
}
