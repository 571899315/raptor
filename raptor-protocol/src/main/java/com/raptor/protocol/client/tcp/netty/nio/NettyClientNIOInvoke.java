package com.raptor.protocol.client.tcp.netty.nio;


import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.protocol.client.AbstractInvoker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/***
 * netty nio 实现
 */
public class NettyClientNIOInvoke extends AbstractInvoker {


    private static final Logger log = LoggerFactory.getLogger(NettyClientNIOInvoke.class);



    @Override
    public RPCResponse doInvoke(RPCRequest request, ClientConfig config) throws Exception {
        InetSocketAddress serviceAddress = new InetSocketAddress(request.getHost(),request.getPort());
		// Get channel by service address
		Channel channel = ChannelManager.getInstance().getChannel(serviceAddress);
		if (null == channel) {
			throw new RuntimeException("Cann't get channel for address" + serviceAddress);
		}
		// Send request
		RPCResponse response = sendRequest(channel, request,config);
		if (response == null) {
			throw new RuntimeException("response is null");
		}
		if (response.hasException()) {
			throw response.getException();
		}
        return response;
    }


    private RPCResponse sendRequest(Channel channel, RPCRequest request, ClientConfig config) {
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
			RPCResponse rpcResponse = rpcResponseFuture.get(1, TimeUnit.SECONDS);
			return rpcResponse;
		} catch (Exception e) {
			log.warn("Exception:", e);
			return null;
		}
	}
}
