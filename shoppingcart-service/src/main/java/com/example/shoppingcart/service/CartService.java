package com.example.shoppingcart.service;

import com.example.shoppingcart.controller.MenuClient;
import com.example.shoppingcart.model.Cart;
import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MenuClient menuClient; // 用于校验商品是否存在

    public Cart addItemToCart(Long userId, Long vendorId,
                              Long productId, String name, String imageUrl,
                              Integer quantity, Double price) {
        if (!menuClient.existsInMenu(vendorId, productId)) {
            throw new IllegalArgumentException("商品不存在于菜单中");
        }
        Cart cart = cartRepository.findByUserIdAndVendorId(userId, vendorId)
                .orElseGet(() -> newCart(userId, vendorId));

        boolean found = false;
        for (CartItem ci : cart.getItems()) {
            if (ci.getProductId().equals(productId)) {
                ci.setQuantity(ci.getQuantity() + quantity);
                ci.setPrice(price);
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem ci = new CartItem();
            ci.setProductId(productId);
            ci.setName(name);
            ci.setImageUrl(imageUrl);
            ci.setQuantity(quantity);
            ci.setPrice(price);
            cart.addItem(ci);
        }

        return cartRepository.save(cart);
    }

    private Cart newCart(Long userId, Long vendorId) {
        Cart cart = new Cart();
        cart.setId(userId + ":" + vendorId);  // Redis主键唯一
        cart.setUserId(userId);
        cart.setVendorId(vendorId);
        return cart;
    }


    public Cart removeItemFromCart(Long userId, Long vendorId, Long productId) {
        Optional<Cart> optionalCart = cartRepository.findByUserIdAndVendorId(userId, vendorId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.removeItem(productId);
            return cartRepository.save(cart);
        }
        return null;
    }

    public void clearCart(Long userId, Long vendorId) {
        cartRepository.findByUserIdAndVendorId(userId, vendorId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    public Cart getCart(Long userId, Long vendorId) {
        return cartRepository.findByUserIdAndVendorId(userId, vendorId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setId(userId.toString()); // Redis 主键
            cart.setUserId(userId);
            cart.setVendorId(vendorId);
            return cartRepository.save(cart);
        });
    }

}
