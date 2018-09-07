package com.raptor.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.raptor.common.config.RaptorConstants;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Inherited
public @interface RaptorService {
	Class<?> value();

	String version() default RaptorConstants.LASTEST_VERSION;

	String contextPath() default RaptorConstants.CONTEXT_PATH;
	int connectTimeoutMillis() default RaptorConstants.DEFAULT_connectTimeoutMillis;

	int readTimeoutMillis() default RaptorConstants.DEFAULT_readTimeoutMillis;

	int retryCount() default RaptorConstants.DEFAULT_RETRY_COUNT;

	String lbStrategy() default RaptorConstants.LB_RANDOM;

	String haStrategy() default RaptorConstants.HA_FAILFAST;

	String threadpool() default RaptorConstants.rpcx_threadpool;

	String cluster() default RaptorConstants.CLUSTER_TYPE;

	String invoker() default RaptorConstants.INVOKER_TYPE;

	String proxy() default RaptorConstants.PROXY;

	boolean sync() default true;

	boolean localFirst() default false;

}
