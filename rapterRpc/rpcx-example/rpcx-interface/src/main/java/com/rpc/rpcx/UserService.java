package com.rpc.rpcx;

import com.raptor.common.annotation.RaptorClient;

@RaptorClient(UserService.class)
public interface UserService {


    UserResponse getRequest(UserRequest request);


}
