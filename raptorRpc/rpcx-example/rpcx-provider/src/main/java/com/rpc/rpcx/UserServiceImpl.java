package com.rpc.rpcx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.raptor.common.annotation.RaptorServer;

import java.util.ArrayList;
import java.util.List;

@Service
@RaptorServer(UserServiceImpl.class)
public class UserServiceImpl implements UserService {


    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);



    @Override
    public UserResponse getRequest(UserRequest request) {
    	logger.info("come");
        UserResponse response = new UserResponse();
        response.setAge("99");
        response.setName("kaka");
        List<Address> addressList = new ArrayList<>();
        for(int i=0;i<100000;i++){
            Address address = new Address();
            address.setCode(i);
            address.setAddress("123");
            addressList.add(address);
        }
        response.setAddressList(addressList);
        return response;
    }
}
