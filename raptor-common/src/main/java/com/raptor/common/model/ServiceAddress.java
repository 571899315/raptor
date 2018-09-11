package com.raptor.common.model;

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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

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

	@Override
	public String toString(){
		return ip + ":" + port;
	}
}
