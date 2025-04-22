package com.example.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "menu-service", url = "${menu.service.url}")
public interface MenuClient {
    @GetMapping("/api/menu/has")
    boolean existsInMenu(@RequestParam("userId") Long vendorId,
                         @RequestParam("productId") Long productId);
}
