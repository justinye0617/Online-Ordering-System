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
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return newCart;
        });

        boolean itemExists = false;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                itemExists = true;
                break;
            }
        }
        if (!itemExists) {
            CartItem newItem = new CartItem();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cart.addItem(newItem);
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

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }
}