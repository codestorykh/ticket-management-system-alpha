package com.codestorykh.order.service;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.dto.EmptyObject;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.order.client.PaymentClient;
import com.codestorykh.order.client.UserClient;
import com.codestorykh.order.dto.OrderConfirmedEvent;
import com.codestorykh.order.dto.OrderRequest;
import com.codestorykh.order.dto.PaymentRequest;
import com.codestorykh.order.entity.Order;
import com.codestorykh.order.enumz.OrderStatus;
import com.codestorykh.order.enumz.PaymentMethod;
import com.codestorykh.order.mapper.OrderMapper;
import com.codestorykh.order.producer.OrderConfirmedKafkaProducer;
import com.codestorykh.order.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final PaymentClient paymentClient;
    private final OrderRepository orderRepository;
    private final OrderConfirmedKafkaProducer orderConfirmedKafkaProducer;

    public OrderServiceImpl(OrderMapper orderMapper,
                            UserClient userClient, PaymentClient paymentClient,
                            OrderRepository orderRepository,
                            OrderConfirmedKafkaProducer orderConfirmedKafkaProducer) {
        this.orderMapper = orderMapper;
        this.userClient = userClient;
        this.paymentClient = paymentClient;
        this.orderRepository = orderRepository;
        this.orderConfirmedKafkaProducer = orderConfirmedKafkaProducer;
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

        // Process payment: can move to new method or service
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(order.getId());
        paymentRequest.setUsername(username);
        paymentRequest.setAmount(orderRequest.getAmount());
        paymentRequest.setCurrency("USD");
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        paymentRequest.setDescription("Payment for order ID: " + order.getId());

        ResponseErrorTemplate paymentResponse = paymentClient.processingPayment(paymentRequest)
                .block();

        if(paymentResponse == null || paymentResponse.isError()) {
            log.error("Payment processing failed for order ID: {}", order.getId());
            return new ResponseErrorTemplate(
                    ApiConstant.PAYMENT_FAILED.getDescription(),
                    ApiConstant.PAYMENT_FAILED.getKey(),
                    new EmptyObject(),
                    true);
        }

        order.setOrderStatus(OrderStatus.COMPLETED);
        Integer paymentId = (Integer) ((LinkedHashMap<?, ?>) paymentResponse.data()).get("paymentId");
        order.setPaymentId(Long.valueOf(paymentId));
        orderRepository.save(order);

        // Send order confirmed event to Kafka
        OrderConfirmedEvent orderConfirmedEvent = new OrderConfirmedEvent();
        orderConfirmedEvent.setOrderId(order.getId());
        orderConfirmedEvent.setUsername(order.getUsername());
        orderConfirmedEvent.setEmail("codestorykh@gmail.com"); // need to get from user service
        orderConfirmedEvent.setPhoneNumber("0123456789"); // need to get from user service
        orderConfirmedEvent.setEventTitle(orderRequest.getEventId().toString()); // need to get from event service
        orderConfirmedEvent.setEventLocation(orderRequest.getEventId().toString()); // need to get from event service
        orderConfirmedEvent.setEventDate(LocalDateTime.now());
        orderConfirmedEvent.setQuantity(orderRequest.getQuantity());
        orderConfirmedEvent.setAmount(orderRequest.getAmount());

        orderConfirmedKafkaProducer.send(orderConfirmedEvent);

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
