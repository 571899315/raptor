package com.raptor.protocol.client.tcp.netty.nio;

import com.raptor.codec.coder.RPCDecoder;
import com.raptor.codec.coder.RPCEncoder;
import com.raptor.codec.serialization.impl.ProtobufSerializer;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelManager {

	private static final Logger log = LoggerFactory.getLogger(ChannelManager.class);

	/**
	 * Singleton
	 */
	private static ChannelManager channelManager;

	private ChannelManager(){}

	public static ChannelManager getInstance() {
		if (channelManager == null) {
			synchronized (ChannelManager.class) {
				if (channelManager == null) {
					channelManager = new ChannelManager();
				}
			}
		}
		return channelManager;
	}

	private Map<InetSocketAddress, Channel> channels = new ConcurrentHashMap<>();

	public Channel getChannel(InetSocketAddress inetSocketAddress) {
		Channel channel = channels.get(inetSocketAddress);
		if (null == channel) {
			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group)
						.channel(NioSocketChannel.class)
						.handler(new RPCChannelInitializer())
						.option(ChannelOption.SO_KEEPALIVE, true);

				channel = bootstrap.connect(inetSocketAddress.getHostName(), inetSocketAddress.getPort()).sync()
						.channel();
				registerChannel(inetSocketAddress, channel);

				// Remove the channel for map when it's closed
				channel.closeFuture().addListener((ChannelFutureListener) future -> removeChannel(inetSocketAddress));
			} catch (Exception e) {
				log.warn("Fail to get channel for address: {}", inetSocketAddress);
			}
		}
		return channel;
	}

	private void registerChannel(InetSocketAddress inetSocketAddress, Channel channel) {
		channels.put(inetSocketAddress, channel);
	}

	private void removeChannel(InetSocketAddress inetSocketAddress) {
		channels.remove(inetSocketAddress);
	}

	private class RPCChannelInitializer extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new RPCEncoder(RPCRequest.class, new ProtobufSerializer()));
			pipeline.addLast(new RPCDecoder(RPCResponse.class, new ProtobufSerializer()));
			pipeline.addLast(new RPCResponseHandler());
		}
	}

	private class RPCResponseHandler extends SimpleChannelInboundHandler<RPCResponse> {

		@Override
		public void channelRead0(ChannelHandlerContext ctx, RPCResponse response) throws Exception {
			ResponseFutureManager.getInstance().futureDone(response);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			log.warn("RPC request exception: {}", cause);
		}
	}
}
