package com.rpc.rpcx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.raptor.common.annotation.RaptorServer;

@Service
@RaptorServer(UserServiceImpl.class)
public class UserServiceImpl implements UserService {


    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);



    @Override
    public UserResponse getRequest(UserRequest request) {

        UserResponse response = new UserResponse();
        response.setAge("99");
        response.setName("kaka");
        return response;
    }
}
