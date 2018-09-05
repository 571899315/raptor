package com.raptor.common.model;

import lombok.Data;

@Data
public class RPCResponse {

    private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasException() {
        return exception != null;
    }
}
