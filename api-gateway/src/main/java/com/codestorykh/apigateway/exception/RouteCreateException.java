package com.codestorykh.apigateway.exception;

public class RouteCreateException extends BaseException{

    public RouteCreateException(String message) {
        super(message);
    }

    public RouteCreateException(String errorCode, String message) {
        super(errorCode, message);
    }
}
