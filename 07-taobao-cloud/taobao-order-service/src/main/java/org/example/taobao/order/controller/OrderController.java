package org.example.taobao.order.controller;

import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.order.OrderCreateResponse;
import org.example.taobao.common.dto.order.OrderDetailDTO;
import org.example.taobao.order.security.UserContextHolder;
import org.example.taobao.order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单接口控制器。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 购物车勾选项下单接口。
     */
    @PostMapping("/from-cart")
    public ApiResponse<OrderCreateResponse> createFromCart() {
        return ApiResponse.success(orderService.createFromCart(UserContextHolder.requireUserId()));
    }

    /**
     * 查询当前登录用户订单详情。
     */
    @GetMapping("/{orderNo}")
    public ApiResponse<OrderDetailDTO> detail(@PathVariable String orderNo) {
        return ApiResponse.success(orderService.queryDetail(UserContextHolder.requireUserId(), orderNo));
    }
}
