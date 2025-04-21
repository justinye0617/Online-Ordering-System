package com.example.shoppingcart.model;

import lombok.Data;
@Data
public class CartItem {
    private Long productId;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Double price;
}
