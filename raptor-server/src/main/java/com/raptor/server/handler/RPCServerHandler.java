package com.raptor.server.handler;

import com.raptor.registry.impl.consul.ConsulServiceDiscovery;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Handle the RPC request
 *
 * @author hongbin Created on 21/10/2017
 */
public class RPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {

	private static final Logger log = LoggerFactory.getLogger(RPCServerHandler.class);

	private Map<String, Object> handlerMap;

	public RPCServerHandler() {
	}

	public RPCServerHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	public void channelRead0(final ChannelHandlerContext ctx, RPCRequest request) throws Exception {
		log.debug("Get request: {}", request);
		RPCResponse response = new RPCResponse();
		response.setRequestId(request.getRequestId());
		try {
			Object result = handleRequest(request);
			response.setResult(result);
		} catch (Exception e) {
			log.warn("Get exception when hanlding request, exception: {}", e);
			response.setException(e);
		}
		ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
			log.debug("Sent response for request: {}", request.getRequestId());
		});
	}

	private Object handleRequest(RPCRequest request) throws Exception {
		log.debug("handleRequest begin");
		// Get service bean
		String serviceName = request.getInterfaceName();
		String verson = request.getVersion();
		Object serviceBean = handlerMap.get(serviceName + "_" + verson);
		if (serviceBean == null) {
			throw new RuntimeException(String.format("No service bean available: %s", serviceName));
		}
		// Invoke by reflect
		Class<?> serviceClass = serviceBean.getClass();
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();
		Method method = serviceClass.getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		log.debug("handleRequest end");
		return method.invoke(serviceBean, parameters);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error("server caught exception", cause);
		ctx.close();
	}
}
