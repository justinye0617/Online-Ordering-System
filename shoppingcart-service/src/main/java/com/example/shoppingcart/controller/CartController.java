package com.example.shoppingcart.controller;

import com.example.shoppingcart.model.Cart;
import com.example.shoppingcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Cart> addItem(@RequestParam Long userId,
                                        @RequestParam Long vendorId,
                                        @RequestParam Long productId,
                                        @RequestParam String name,
                                        @RequestParam String imageUrl,
                                        @RequestParam Integer quantity,
                                        @RequestParam Double price) {
        Cart cart = cartService.addItemToCart(userId, vendorId, productId, name, imageUrl, quantity, price);
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/remove")
    public ResponseEntity<Cart> removeItem(@RequestParam Long userId,
                                           @RequestParam Long vendorId,
                                           @RequestParam Long productId) {
        Cart cart = cartService.removeItemFromCart(userId, vendorId, productId);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/view")
    public ResponseEntity<Cart> viewCart(@RequestParam Long userId, @RequestParam Long vendorId) {
        Cart cart = cartService.getCart(userId, vendorId);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(@RequestParam Long userId, @RequestParam Long vendorId) {
        cartService.clearCart(userId, vendorId);
        return ResponseEntity.ok().build();
    }
}