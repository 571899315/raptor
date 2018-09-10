package com.rpc.rpcx;

import com.raptor.common.annotation.RaptorService;

@RaptorService(UserService.class)
public interface UserService {


    UserResponse getRequest(UserRequest request);


}
