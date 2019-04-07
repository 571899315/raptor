package com.raptor.loadbalancer;


import com.raptor.common.annotation.SPI;

@SPI("default")
public interface Cluster extends Invoker {

    void setLoadBalance(LoadBalance loadBalance);

    void setHaStrategy(HaStrategy haStrategy);

}
