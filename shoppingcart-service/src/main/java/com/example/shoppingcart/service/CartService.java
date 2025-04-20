package com.example.shoppingcart.service;

import com.example.shoppingcart.model.Cart;
import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.repository.CartRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Transactional
    public Cart addItemToCart(Long userId,
                              Long productId,
                              String name,
                              String imageUrl,
                              Integer quantity,
                              Double price) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart(); c.setUserId(userId); return c;
        });

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


    @Transactional
    public Cart removeItemFromCart(Long userId, Long productId) {
        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.getItems().removeIf(item -> item.getProductId().equals(productId));
            return cartRepository.save(cart);
        }
        return null;
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId)
                .ifPresent(cart -> {
                    cart.getItems().clear();
                    cartRepository.save(cart);
                });
    }


    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }
}