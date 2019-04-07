package com.raptor.protocol.client.tcp.netty.nio;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.protocol.client.AbstractInvoker;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public class NettyClientNIOInvoke extends AbstractInvoker {

	private static final Logger log = LoggerFactory.getLogger(NettyClientNIOInvoke.class);

	private AtomicLong count = new AtomicLong(0);

	@Override
	public RPCResponse doInvoke(RPCRequest request, ClientConfig config) throws Exception {
		InetSocketAddress serviceAddress = new InetSocketAddress(request.getHost(), request.getPort());
		// Get channel by service address

		RPCResponse response = null;
		boolean sync = config.isSync();

		if (sync) {
			Channel channel = ChannelManager.getInstance().getChannel(serviceAddress);
			if (null == channel) {
				throw new RuntimeException("Cann't get channel for address" + serviceAddress);
			}
			// Send request
			response = sendRequest(channel, request, config);
			if (response == null) {
				throw new RuntimeException("response is null");
			}
			if (response.hasException()) {
				throw response.getException();
			}
		} else {

		}
		return response;
	}

	private RPCResponse sendRequest(Channel channel, RPCRequest request, ClientConfig config) {
		RPCResponse rpcResponse = null;
		CountDownLatch latch = new CountDownLatch(1);
		RPCResponseFuture rpcResponseFuture = new RPCResponseFuture();
		rpcResponseFuture.setRequestId(request.getRequestId());
		ResponseFutureManager.getInstance().registerFuture(rpcResponseFuture);
		channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
			latch.countDown();
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		try {
			rpcResponse = rpcResponseFuture.get(config.getReadTimeoutMillis(), TimeUnit.MILLISECONDS);
			Long result = count.incrementAndGet();
			log.info("result=" + result);
			return rpcResponse;
		} catch (Exception e) {
			log.error("Exception:", e);
			rpcResponse = new RPCResponse();
			rpcResponse.setException(e);
			return rpcResponse;
		}
	}
}
