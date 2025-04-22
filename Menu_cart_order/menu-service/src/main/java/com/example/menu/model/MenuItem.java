package com.example.menu.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
@Entity
@Data
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;          // 自动生成，即商品 ID，可供 Cart / Order 引用

    private String name;      // 菜名
    private Double price;
    private String imageUrl;  // 菜品图片 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    @JsonBackReference
    private Menu menu;
}
