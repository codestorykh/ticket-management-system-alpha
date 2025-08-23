package com.codestorykh.order.service;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.dto.EmptyObject;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.order.client.UserClient;
import com.codestorykh.order.dto.OrderRequest;
import com.codestorykh.order.entity.Order;
import com.codestorykh.order.enumz.OrderStatus;
import com.codestorykh.order.mapper.OrderMapper;
import com.codestorykh.order.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderMapper orderMapper,
                            UserClient userClient,
                            OrderRepository orderRepository) {
        this.orderMapper = orderMapper;
        this.userClient = userClient;
        this.orderRepository = orderRepository;
    }

    @Override
    public ResponseErrorTemplate createOrder(OrderRequest orderRequest, HttpServletRequest httpServletRequest) {
        // Check user authentication and authorization
        String username = handleUnauthorized(httpServletRequest);
        if(!StringUtils.hasText(username)) {
            return new ResponseErrorTemplate(
                    ApiConstant.UN_AUTHORIZATION.getDescription(),
                    ApiConstant.UN_AUTHORIZATION.getKey(),
                    new EmptyObject(),
                    true);
        }
        // Payment processing logic would go here

        // Create order in the database
        Order order = orderMapper.toEntity(orderRequest);
        order.setEventId(orderRequest.getEventId()); // need to check event service
        order.setTicketId(orderRequest.getTicketId()); // need to check ticket service
        order.setUsername(username);
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                orderMapper.toResponse(order),
                false);
    }

    @Override
    public ResponseErrorTemplate getOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.map(value -> new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                orderMapper.toResponse(value),
                false)).orElse(null);
    }

    @Override
    public ResponseErrorTemplate cancelOrder(Long orderId, HttpServletRequest httpServletRequest) {
        // Check user authentication and authorization
        String username = handleUnauthorized(httpServletRequest);
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isEmpty()) {
            return new ResponseErrorTemplate(
                    ApiConstant.DATA_NOT_FOUND.getDescription(),
                    ApiConstant.DATA_NOT_FOUND.getKey(),
                    new EmptyObject(),
                    true);
        }
        order.get().setOrderStatus(OrderStatus.CANCELLED);
        order.get().setUpdatedBy(username);
        orderRepository.save(order.get());

        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                new EmptyObject(),
                false);
    }

    private String handleUnauthorized(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        var user = userClient.verifyToken(token)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Invalid token"));
       if(user != null && "TOKEN_VALID".equalsIgnoreCase(user.getCode())) {
            return user.getData().get("username").toString();
       }
    log.warn("Invalid token provided");
    return null;
    }
}
