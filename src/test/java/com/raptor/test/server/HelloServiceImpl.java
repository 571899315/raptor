package com.raptor.test.server;

import com.raptor.test.client.HelloService;
import com.raptor.test.client.Person;
import com.raptor.server.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    public HelloServiceImpl(){

    }

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
