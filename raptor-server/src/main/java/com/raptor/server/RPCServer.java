package com.raptor.server;

import com.raptor.codec.coder.RPCDecoder;
import com.raptor.codec.coder.RPCEncoder;
import com.raptor.codec.serialization.impl.ProtobufSerializer;
import com.raptor.common.annotation.RaptorService;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.model.ServiceAddress;
import com.raptor.registry.ServiceRegistry;
import com.raptor.server.handler.RPCServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hongbin
 * Created on 21/10/2017
 */
public class RPCServer implements ApplicationContextAware, InitializingBean {

    //@NonNull
    private String serverIp;
    //@NonNull
    private int serverPort;
	//@NonNull
	private ServiceRegistry serviceRegistry;

	private Map<String, Object> handlerMap = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger(RPCServer.class);




	public RPCServer(String serverIp, int serverPort,ServiceRegistry serviceRegistry) {
		super();
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		log.info("Putting handler");
		// Register handler
		getServiceInterfaces(ctx)
				.stream()
				.forEach(interfaceClazz -> {
					String serviceName = interfaceClazz.getAnnotation(RaptorService.class).value().getName();

					try{
						Class<?> clazz = Class.forName(serviceName);
						boolean annotationPresent = clazz.isAnnotationPresent(RaptorService.class);
						if(annotationPresent) {
							RaptorService annotation = clazz.getAnnotation(RaptorService.class);
							String verson = annotation.version();
							Object serviceBean = ctx.getBean(interfaceClazz);
							handlerMap.put(serviceName+"_"+verson, serviceBean);
							log.debug("Put handler: {}, {}", serviceName, serviceBean);
						}


					}catch (Exception e){

					}

				});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		startServer();
	}

	private void startServer() {
		// Get ip and port
		log.debug("Starting server on port: {}", serverPort);
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel) throws Exception {
							ChannelPipeline pipeline = channel.pipeline();
							pipeline.addLast(new RPCDecoder(RPCRequest.class, new ProtobufSerializer()));
							pipeline.addLast(new RPCEncoder(RPCResponse.class, new ProtobufSerializer()));
							pipeline.addLast(new RPCServerHandler(handlerMap));
						}
					});
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture future = bootstrap.bind(serverIp, serverPort).sync();

			registerServices();

			log.info("Server started");

			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			throw new RuntimeException("Server shutdown!", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	private void registerServices() {
		if (serviceRegistry != null) {
			for (String interfaceName : handlerMap.keySet()) {
				String[] names = interfaceName.split("_");
				ServiceAddress address = new ServiceAddress(serverIp, serverPort);
				address.setName(names[0]);
				address.setVersion(names[1]);
				serviceRegistry.register(address);
				log.info("Registering service: {} with address: {}:{}", interfaceName, serverIp, serverPort);
			}
		}
	}

	private List<Class<?>> getServiceInterfaces(ApplicationContext ctx) {
		Class<? extends Annotation> clazz = RaptorService.class;
		return ctx.getBeansWithAnnotation(clazz)
				.values().stream()
				.map(AopUtils::getTargetClass)
				.map(cls -> Arrays.asList(cls.getInterfaces()))
				.flatMap(List::stream)
				.filter(cls -> Objects.nonNull(cls.getAnnotation(clazz)))
				.collect(Collectors.toList());
	}
}
