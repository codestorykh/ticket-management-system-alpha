package com.codestorykh.order.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.order.dto.OrderRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {

    ResponseErrorTemplate createOrder(OrderRequest orderRequest, HttpServletRequest httpServletRequest);
    ResponseErrorTemplate getOrderById(Long orderId);
    ResponseErrorTemplate cancelOrder(Long orderId, HttpServletRequest httpServletRequest);

}
