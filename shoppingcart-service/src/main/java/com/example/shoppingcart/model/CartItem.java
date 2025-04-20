package com.example.shoppingcart.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

@Entity
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;  // 对应 MenuItem.id
    private String name;     // 菜名
    private String imageUrl; // 图片
    private Integer quantity;
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;
}