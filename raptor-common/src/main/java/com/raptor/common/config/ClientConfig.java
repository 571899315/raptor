package com.raptor.common.config;


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

	private String proxy =RaptorConstants.PROXY;



	private boolean sync = true;

	private boolean localFirst = false;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public int getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}

	public void setConnectTimeoutMillis(int connectTimeoutMillis) {
		this.connectTimeoutMillis = connectTimeoutMillis;
	}

	public int getReadTimeoutMillis() {
		return readTimeoutMillis;
	}

	public void setReadTimeoutMillis(int readTimeoutMillis) {
		this.readTimeoutMillis = readTimeoutMillis;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getLbStrategy() {
		return lbStrategy;
	}

	public void setLbStrategy(String lbStrategy) {
		this.lbStrategy = lbStrategy;
	}

	public String getHaStrategy() {
		return haStrategy;
	}

	public void setHaStrategy(String haStrategy) {
		this.haStrategy = haStrategy;
	}

	public String getThreadpool() {
		return threadpool;
	}

	public void setThreadpool(String threadpool) {
		this.threadpool = threadpool;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getInvoker() {
		return invoker;
	}

	public void setInvoker(String invoker) {
		this.invoker = invoker;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public boolean isLocalFirst() {
		return localFirst;
	}

	public void setLocalFirst(boolean localFirst) {
		this.localFirst = localFirst;
	}
}
