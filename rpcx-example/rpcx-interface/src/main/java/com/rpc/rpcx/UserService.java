package com.rpc.rpcx;

import com.raptor.common.annotation.RaptorServer;

@RaptorServer(UserService.class)
public interface UserService {


    UserResponse getRequest(UserRequest request);


}
