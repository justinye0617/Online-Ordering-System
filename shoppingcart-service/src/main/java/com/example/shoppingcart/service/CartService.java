package com.example.shoppingcart.service;

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

    public Cart addItemToCart(Long userId,
                              Long productId,
                              String name,
                              String imageUrl,
                              Integer quantity,
                              Double price) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> newCart(userId));

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

    public Cart removeItemFromCart(Long userId, Long productId) {
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.removeItem(productId);
            return cartRepository.save(cart);
        }
        return null;
    }

    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setId(userId.toString()); // Redis 主键
            cart.setUserId(userId);
            return cartRepository.save(cart);
        });
    }


    private Cart newCart(Long userId) {
        Cart cart = new Cart();
        cart.setId(userId.toString()); // 以 userId 为 Redis 主键
        cart.setUserId(userId);
        return cart;
    }
}
