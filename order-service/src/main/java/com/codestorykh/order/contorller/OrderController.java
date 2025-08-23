package com.codestorykh.order.contorller;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.order.dto.OrderRequest;
import com.codestorykh.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping()
    public ResponseEntity<ResponseErrorTemplate> create(@RequestBody OrderRequest orderRequest,
                                                        HttpServletRequest httpServletRequest) {
        log.info("Intercept create new order with req: {}", orderRequest);
        return ResponseEntity.ok(orderService.createOrder(orderRequest, httpServletRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> getById(@PathVariable Long id) {
        log.info("Intercept get order by id: {}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> lockTicket(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        log.info("Intercept cancel id: {}", id);
        return ResponseEntity.ok(orderService.cancelOrder(id, httpServletRequest));
    }
}
