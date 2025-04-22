package com.example.order.client;

import com.example.order.dto.CartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "shoppingcart-service", url = "${cart.service.url}")
public interface CartClient {

    @GetMapping("/api/cart/view")
    CartDTO getCart(@RequestParam("userId") Long userId, @RequestParam("vendorId") Long vendorId);

    @DeleteMapping("/api/cart/clear")
    void clearCart(@RequestParam("userId") Long userId, @RequestParam("vendorId") Long vendorId);
}
