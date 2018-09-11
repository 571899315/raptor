
package com.rpc.rpcx.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.alibaba.fastjson.JSON;
import com.rpc.rpcx.UserRequest;
import com.rpc.rpcx.UserResponse;
import com.rpc.rpcx.UserService;
import com.rpc.rpcx.util.SpringUtils;

@Controller
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private UserService userService;

	@GetMapping(value = "test")
	public String hello() {
		String result = "";
		UserRequest request = new UserRequest();
		UserResponse response = userService.getRequest(request);
		logger.info("response is:" + JSON.toJSONString(response));
		return result;
	}

}