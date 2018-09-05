package com.raptor.loadbalancer;

import com.raptor.common.annotation.SPI;
import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;


@SPI("default")
public interface Invoker {

	RPCResponse invoke(RPCRequest request, ClientConfig config) throws Exception;

}
