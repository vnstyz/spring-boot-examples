package org.example.taobao.cart.controller;

import jakarta.validation.Valid;
import org.example.taobao.cart.security.UserContextHolder;
import org.example.taobao.cart.service.CartService;
import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.cart.AddCartItemRequest;
import org.example.taobao.common.dto.cart.CartItemDTO;
import org.example.taobao.common.dto.cart.CartSettleDTO;
import org.example.taobao.common.dto.cart.CheckedCartItemDTO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 购物车接口控制器。
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 新增商品到购物车。
     */
    @PostMapping("/items")
    public ApiResponse<Void> add(@Valid @RequestBody AddCartItemRequest request) {
        cartService.addItem(UserContextHolder.requireUserId(), request);
        return ApiResponse.success(null);
    }

    /**
     * 查询当前用户购物车。
     */
    @GetMapping("/items")
    public ApiResponse<List<CartItemDTO>> list() {
        return ApiResponse.success(cartService.listCart(UserContextHolder.requireUserId()));
    }

    /**
     * 修改购物车项勾选状态。
     */
    @PatchMapping("/items/{cartItemId}/checked")
    public ApiResponse<Void> checked(@PathVariable Long cartItemId, @RequestParam("checked") boolean checked) {
        cartService.changeChecked(UserContextHolder.requireUserId(), cartItemId, checked);
        return ApiResponse.success(null);
    }

    /**
     * 结算当前用户已勾选商品。
     */
    @PostMapping("/settle")
    public ApiResponse<CartSettleDTO> settle() {
        return ApiResponse.success(cartService.settle(UserContextHolder.requireUserId()));
    }

    /**
     * 清空当前用户已勾选商品。
     */
    @DeleteMapping("/checked")
    public ApiResponse<Void> clearChecked() {
        cartService.clearChecked(UserContextHolder.requireUserId());
        return ApiResponse.success(null);
    }

    /**
     * 内部接口：订单服务读取用户勾选购物车项。
     */
    @GetMapping("/internal/checked/{userId}")
    public ApiResponse<List<CheckedCartItemDTO>> internalChecked(@PathVariable Long userId) {
        return ApiResponse.success(cartService.queryCheckedForOrder(userId));
    }

    /**
     * 内部接口：订单创建成功后清空用户勾选项。
     */
    @DeleteMapping("/internal/checked/{userId}")
    public ApiResponse<Void> internalClearChecked(@PathVariable Long userId) {
        cartService.clearChecked(userId);
        return ApiResponse.success(null);
    }
}
