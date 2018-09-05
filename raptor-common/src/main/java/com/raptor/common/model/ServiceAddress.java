package com.raptor.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceAddress {

	private String name;
	private String version;
	private String ip;
	private int port;


	public ServiceAddress() {
	}

	@Override
	public String toString(){
		return ip + ":" + port;
	}
}
