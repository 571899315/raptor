package com.raptor.common.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author hongbin
 * Created on 21/10/2017
 */
@Data
@Builder
public class RPCRequest {

	private String host;
	private int port;
	private String requestId;
	private String interfaceName;

	private String version;

	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;
}