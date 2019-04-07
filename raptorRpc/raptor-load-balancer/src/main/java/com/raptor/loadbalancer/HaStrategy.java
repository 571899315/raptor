package com.raptor.loadbalancer;

import com.raptor.common.annotation.SPI;
import com.raptor.common.config.ClientConfig;
import com.raptor.common.config.RaptorConstants;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.loadbalancer.LoadBalance;


@SPI(RaptorConstants.HA_FAILFAST)
public interface HaStrategy {

	RPCResponse invoke(RPCRequest request, ClientConfig config , LoadBalance loadBalance) throws Exception;

}
