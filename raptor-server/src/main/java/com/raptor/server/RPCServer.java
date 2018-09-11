package com.raptor.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.raptor.codec.coder.RPCDecoder;
import com.raptor.codec.coder.RPCEncoder;
import com.raptor.codec.serialization.impl.ProtobufSerializer;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.model.ServiceAddress;
import com.raptor.common.util.AnnotationUtil;
import com.raptor.registry.ServiceRegistry;
import com.raptor.server.handler.RPCServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RPCServer implements ApplicationContextAware, InitializingBean {

	// @NonNull
	private String serverIp;
	// @NonNull
	private int serverPort;
	// @NonNull
	private ServiceRegistry serviceRegistry;

	private Map<String, Object> handlerMap = new ConcurrentHashMap<>();
	
	private String[] packageNames;
	
	
	

	private static final Logger log = LoggerFactory.getLogger(RPCServer.class);

	public RPCServer(String serverIp, int serverPort,String[] packageNames, ServiceRegistry serviceRegistry) {
		super();
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.packageNames = packageNames;
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		log.info("Putting handler");
		
		List<Class<?>> listByAnnotation = AnnotationUtil.listByAnnotation(packageNames);
		
		for(Class<?> clazz:listByAnnotation) {
			Object serviceBean = ctx.getBean(clazz);
			if(serviceBean != null) {
				handlerMap.putIfAbsent(clazz.getName(), serviceBean);
			}
		}
		
//		if(handlerMap.isEmpty()||handlerMap.size()==0) {
//			throw new NestedRuntimeException("map is null");
//		}
		// Register handler
//		getServiceInterfaces(ctx).stream().forEach(interfaceClazz -> {
//			String serviceName = interfaceClazz.getAnnotation(RaptorService.class).value().getName();
//
//			try {
//				Class<?> clazz = Class.forName(serviceName);
//				boolean annotationPresent = clazz.isAnnotationPresent(RaptorService.class);
//				if (annotationPresent) {
//					RaptorService annotation = clazz.getAnnotation(RaptorService.class);
//					String verson = annotation.version();
//					Object serviceBean = ctx.getBean(interfaceClazz);
//					handlerMap.put(serviceName + "_" + verson, serviceBean);
//					log.debug("Put handler: {}, {}", serviceName, serviceBean);
//				}
//
//			} catch (Exception e) {
//
//			}
//
//		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		startServer();
	}

	private void startServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				log.debug("Starting server on port: {}", serverPort);
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();
				try {
					ServerBootstrap bootstrap = new ServerBootstrap();
					bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
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
				} catch (Exception e) {
					throw new RuntimeException("Server shutdown!", e);
				} finally {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
				}
			}
		}) {
		}.start();
	}

	private void registerServices() throws Exception {
		if (serviceRegistry != null) {
			if (handlerMap.isEmpty() || handlerMap.size() == 0) {
				throw new Exception("map is null");
			}

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
		return null;
//		Class<? extends Annotation> clazz = RaptorService.class;
//		Map<String, Object> beansWithAnnotation = ctx.getBeansWithAnnotation(clazz);
//		log.info(" map is:"+beansWithAnnotation);
//		
//		return beansWithAnnotation.values().stream().map(AopUtils::getTargetClass).map(cls -> Arrays.asList(cls.getInterfaces())).flatMap(List::stream).filter(cls -> Objects.nonNull(cls.getAnnotation(clazz))).collect(Collectors.toList());
	}
}
