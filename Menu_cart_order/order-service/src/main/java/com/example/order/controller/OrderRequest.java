package com.example.order.controller;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private Long vendorId;
    private List<OrderItemRequest> items;
}
