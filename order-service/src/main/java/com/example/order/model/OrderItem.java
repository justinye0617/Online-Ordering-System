package com.example.order.model;
import javax.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Double price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;
}
