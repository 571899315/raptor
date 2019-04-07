package com.raptor.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import com.raptor.codec.coder.RPCDecoder;
import com.raptor.codec.coder.RPCEncoder;
import com.raptor.codec.serialization.impl.ProtobufSerializer;
import com.raptor.common.annotation.RaptorClient;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.model.ServiceAddress;
import com.raptor.common.util.AnnotationUtil;
import com.raptor.common.util.ClassUtil;
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

public class RaptorServerInit implements ApplicationContextAware, InitializingBean {

	private String serverIp;
	private int serverPort;
	private ServiceRegistry serviceRegistry;
	private Map<String, Object> serverMap = new ConcurrentHashMap<>();
	private Map<String, Object> clientMap = new ConcurrentHashMap<>();
	private String[] serverPackageNames;
	private String[] clientPackageNames;
	private static final Logger log = LoggerFactory.getLogger(RaptorServerInit.class);

	public RaptorServerInit() {}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		List<Class<?>> listByAnnotation = AnnotationUtil.listByAnnotation(serverPackageNames);
		for (Class<?> clazz : listByAnnotation) {
			Object serviceBean = ctx.getBean(clazz);
			if (!clazz.isInterface()) {
				Class<?>[] interfaces = clazz.getInterfaces();
				for (Class<?> cla : interfaces) {
					if (serviceBean != null) {
						serverMap.putIfAbsent(cla.getName(), serviceBean);
					}
				}
			} else {
				if (serviceBean != null) {
					serverMap.putIfAbsent(clazz.getName(), serviceBean);
				}
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		startServer();
		registerServices();
	}

	private void startServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				log.debug("Starting server on port: {}", serverPort);
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();
				try {
					ServerBootstrap bootstrap = new ServerBootstrap();
					if (serverMap.isEmpty() || serverMap.size() == 0) {
						throw new IllegalArgumentException("serverMap is empty");
					}
					bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
							.childHandler(new ChannelInitializer<SocketChannel>() {
								@Override
								public void initChannel(SocketChannel channel) throws Exception {
									ChannelPipeline pipeline = channel.pipeline();
									pipeline.addLast(new RPCDecoder(RPCRequest.class, new ProtobufSerializer()));
									pipeline.addLast(new RPCEncoder(RPCResponse.class, new ProtobufSerializer()));
									pipeline.addLast(new RPCServerHandler(serverMap));
								}
							});
					bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
					bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
					ChannelFuture future = bootstrap.bind(serverIp, serverPort).sync();
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

			List<Class<?>> clsList = ClassUtil.getClasses(clientPackageNames);
			if (clsList != null && clsList.size() > 0) {
				for (Class<?> clazz : clsList) {
					boolean annotation = clazz.isAnnotationPresent(RaptorClient.class);
					if (annotation) {
						RaptorClient client = clazz.getAnnotation(RaptorClient.class);
						Class<?> value = client.value();
						String interfaceName = value.getName();
						String version = client.version();
						clientMap.putIfAbsent(interfaceName + "_" + version, client);
					}
				}
			}
			if (clientMap.isEmpty() || clientMap.size() == 0) {
				throw new IllegalArgumentException("clientMap is empty");
			}
			for (String interfaceName : clientMap.keySet()) {
				String[] names = interfaceName.split("_");
				ServiceAddress address = new ServiceAddress(serverIp, serverPort);
				address.setName(names[0]);
				address.setVersion(names[1]);
				serviceRegistry.register(address);
				log.info("Registering service: {} with address: {}:{}", interfaceName, serverIp, serverPort);
			}
		}
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}



	public String[] getServerPackageNames() {
		return serverPackageNames;
	}

	public void setServerPackageNames(String[] serverPackageNames) {
		this.serverPackageNames = serverPackageNames;
	}

	public String[] getClientPackageNames() {
		return clientPackageNames;
	}

	public void setClientPackageNames(String[] clientPackageNames) {
		this.clientPackageNames = clientPackageNames;
	}
	
	
	
	
	
	
}
