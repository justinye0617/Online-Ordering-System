package com.example.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private Long vendorId;
    private List<CartItemDTO> items;
}
