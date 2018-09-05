package com.raptor.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ServiceAddress {

	private String name;
	private String version;
	private String ip;
	private int port;


	public ServiceAddress() {
	}

	public ServiceAddress(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String toString(){
		return ip + ":" + port;
	}
}
