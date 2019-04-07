package com.raptor.protocol.client.tcp.netty.nio;

import com.raptor.common.model.RPCResponse;

import java.util.concurrent.ConcurrentHashMap;

public class ResponseFutureManager {
	/**
	 * Singleton
	 */
	private static ResponseFutureManager rpcFutureManager;

	private ResponseFutureManager(){}

	public static ResponseFutureManager getInstance() {
		if (rpcFutureManager == null) {
			synchronized (ChannelManager.class) {
				if (rpcFutureManager == null) {
					rpcFutureManager = new ResponseFutureManager();
				}
			}
		}
		return rpcFutureManager;
	}

	private ConcurrentHashMap<String, RPCResponseFuture> rpcFutureMap = new ConcurrentHashMap<>();

	public void registerFuture(RPCResponseFuture rpcResponseFuture) {
		rpcFutureMap.put(rpcResponseFuture.getRequestId(), rpcResponseFuture);
	}

	public void futureDone(RPCResponse response) {
		// Mark the responseFuture as done
		rpcFutureMap.remove(response.getRequestId()).done(response);
	}
}
