package com.raptor.protocol.client.tcp.netty.nio;


import com.raptor.common.config.ClientConfig;
import com.raptor.common.model.RPCRequest;
import com.raptor.common.model.RPCResponse;
import com.raptor.common.model.ServiceAddress;
import com.raptor.loadbalancer.loadbalance.AbstractLoadBalance;

import java.util.List;

/***
 * netty nio 实现
 */
public class NettyClientNIOInvoke extends AbstractLoadBalance {

    @Override
    public RPCResponse doInvoke(RPCRequest request, ClientConfig config, List<ServiceAddress> addresses) throws Exception {

        System.out.print("NettyClientNIOInvoke invoke ");

        return null;
    }
}
