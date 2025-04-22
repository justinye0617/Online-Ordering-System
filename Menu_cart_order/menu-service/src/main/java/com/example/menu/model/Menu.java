package com.example.menu.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MenuItem> items = new ArrayList<>();

    public void addItem(MenuItem item){
        items.add(item);
        item.setMenu(this);
    }

    public void removeItem(MenuItem item){
        items.remove(item);
        item.setMenu(null);
    }
}
