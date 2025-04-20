package com.example.menu.controller;

import com.example.menu.model.Menu;
import com.example.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @PostMapping("/add")
    public ResponseEntity<Menu> addItem(@RequestParam Long userId,
                                        @RequestParam Long productId,
                                        @RequestParam Double price){
        Menu menu = menuService.addItemToMenu(userId, productId, price);
        return ResponseEntity.ok(menu);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Menu> removeItem(@RequestParam Long userId,
                                           @RequestParam Long productId){
        Menu menu = menuService.removeItemFromMenu(userId, productId);
        if(menu == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/view")
    public ResponseEntity<Menu> viewMenu(@RequestParam Long userId){
        Menu menu = menuService.getMenu(userId);
        if( menu == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(menu);
    }
}
