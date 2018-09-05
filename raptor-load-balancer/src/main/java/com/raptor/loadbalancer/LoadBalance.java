package com.raptor.loadbalancer;



import com.raptor.common.annotation.SPI;
import com.raptor.common.config.ClientConfig;
import com.raptor.common.config.RaptorConstants;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.registry.ServiceDiscovery;

import java.util.List;

@SPI(RaptorConstants.LB_RANDOM)
public interface LoadBalance {

    void setRegister(ServiceDiscovery register);

    RPCResponse invoke(RPCRequest request, ClientConfig config, List<FailHost> failHosts) throws Exception;
    
}
