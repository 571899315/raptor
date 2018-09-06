package com.raptor.loadbalancer;

import lombok.Data;

public class FailHost {
	
	private String ip;
	
	private int port;


	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
