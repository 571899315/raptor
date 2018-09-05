package com.raptor.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceAddress {
	private String ip;
	private int port;

	@Override
	public String toString(){
		return ip + ":" + port;
	}
}
