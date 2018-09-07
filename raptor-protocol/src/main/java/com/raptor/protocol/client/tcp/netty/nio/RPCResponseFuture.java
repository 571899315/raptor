package com.raptor.protocol.client.tcp.netty.nio;

import com.raptor.common.model.RPCResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class RPCResponseFuture implements Future<RPCResponse> {
	@NonNull
	private String requestId;

	private RPCResponse response;

	private CountDownLatch latch = new CountDownLatch(1);


	private static final Logger log = LoggerFactory.getLogger(RPCResponseFuture.class);


	public void done(RPCResponse response) {
		this.response = response;
		latch.countDown();
	}

	@Override
	public RPCResponse get() throws InterruptedException, ExecutionException {
		try {
			latch.await();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return response;
	}

	@Override
	public RPCResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		try {
			if (!latch.await(timeout, unit)) {
				throw new TimeoutException("RPC Request timeout!");
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return response;
	}

	@Override
	public boolean isDone() {
		return latch.getCount() == 0;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException();
	}

	public RPCResponseFuture() {
	}

	public RPCResponseFuture(String requestId, RPCResponse response) {
		this.requestId = requestId;
		this.response = response;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public RPCResponse getResponse() {
		return response;
	}

	public void setResponse(RPCResponse response) {
		this.response = response;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
}
