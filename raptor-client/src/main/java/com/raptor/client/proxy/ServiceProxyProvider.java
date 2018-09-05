package com.raptor.client.proxy;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import com.raptor.common.annotation.RaptorService;
import com.raptor.common.config.ClientConfig;
import com.raptor.registry.ServiceDiscovery;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Register proxy bean for required client in bean container. 1. Get interfaces
 * with annotation RPCService 2. Create proxy bean for the interfaces and
 * register them
 *
 */
@Slf4j
//@RequiredArgsConstructor
public class ServiceProxyProvider implements BeanDefinitionRegistryPostProcessor {

	@NonNull
	private ServiceDiscovery serviceDiscovery;

	
	@NonNull
	private String[] basePackages;
	
	
	

	public ServiceProxyProvider(ServiceDiscovery serviceDiscovery, String[] basePackages) {
		this.serviceDiscovery = serviceDiscovery;
		this.basePackages = basePackages;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		log.info("register beans");
		ClassPathScanningCandidateComponentProvider scanner = getScanner();
		scanner.addIncludeFilter(new AnnotationTypeFilter(RaptorService.class));

		for (String basePackage : getBasePackages()) {
			Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
			for (BeanDefinition candidateComponent : candidateComponents) {
				if (candidateComponent instanceof AnnotatedBeanDefinition) {
					AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
					AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();

					BeanDefinitionHolder holder = createBeanDefinition(annotationMetadata);
					BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
				}
			}
		}
	}

	private ClassPathScanningCandidateComponentProvider getScanner() {
		return new ClassPathScanningCandidateComponentProvider(false) {

			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				if (beanDefinition.getMetadata().isIndependent()) {

					if (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().getInterfaceNames().length == 1 && Annotation.class.getName().equals(beanDefinition.getMetadata().getInterfaceNames()[0])) {

						try {
							Class<?> target = Class.forName(beanDefinition.getMetadata().getClassName());
							return !target.isAnnotation();
						} catch (Exception ex) {

							log.error("Could not load target class: {}, {}", beanDefinition.getMetadata().getClassName(), ex);
						}
					}
					return true;
				}
				return false;
			}
		};
	}

	private BeanDefinitionHolder createBeanDefinition(AnnotationMetadata annotationMetadata) {
		String className = annotationMetadata.getClassName();
		log.info("Creating bean definition for class: {}", className);
		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(ClientProxy.class);
		String beanName = StringUtils.uncapitalize(className.substring(className.lastIndexOf('.') + 1));
		try {
			Class<?> clazz = Class.forName(className);
			boolean annotationPresent = clazz.isAnnotationPresent(RaptorService.class);
			if(annotationPresent) {
				RaptorService annotation = clazz.getAnnotation(RaptorService.class);
				ClientConfig clientConfig = ClientConfig.builder()
						.cluster(annotation.cluster())
						.connectTimeoutMillis(annotation.connectTimeoutMillis())
						.contextPath(annotation.contextPath())
						.haStrategy(annotation.haStrategy())
						.lbStrategy(annotation.lbStrategy())
						.localFirst(annotation.localFirst())
						.readTimeoutMillis(annotation.readTimeoutMillis())
						.retryCount(annotation.retryCount())
						.sync(annotation.sync())
						.threadpool(annotation.threadpool())
						.proxy(annotation.proxy())
						.build();
				definition.addPropertyValue("clientConfig", clientConfig);
				
			}
			
		} catch (ClassNotFoundException e) {
			log.error("ClassNotFoundException : {}", e);
		}
		definition.addPropertyValue("type", className);
		definition.addPropertyValue("serviceDiscovery", serviceDiscovery);
		return new BeanDefinitionHolder(definition.getBeanDefinition(), beanName);
	}

	private Set<String> getBasePackages() {
		Set set = new HashSet<>();
		Collections.addAll(set, basePackages);
		return set;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}
}
