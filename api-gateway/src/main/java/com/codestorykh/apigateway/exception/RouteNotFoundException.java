package com.codestorykh.apigateway.exception;


public class RouteNotFoundException extends BaseException {


    public RouteNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }

    public RouteNotFoundException(String message) {
        super(message);
    }
}
