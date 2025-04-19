package com.example.menu.service;

import com.example.menu.model.Menu;
import com.example.menu.model.MenuItem;
import com.example.menu.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;

    @Transactional
    public Menu addItemToMenu(Long userId, Long productId, Double price) {
        Menu menu = menuRepository.findByUserId(userId).orElseGet(() -> {
            Menu newMenu = new Menu();
            newMenu.setUserId(userId);
            return newMenu;
        });
        System.out.println(productId);
        boolean itemExists = false;
        for (MenuItem item : menu.getItems()) {
            if (item.getProductId().equals(productId)) {
                item.setPrice(price);
                itemExists = true;
                break;
            }
        }
        if (!itemExists) {
            MenuItem newItem = new MenuItem();
            newItem.setProductId(productId);
            newItem.setPrice(price);
            menu.addItem(newItem);
        }
        return menuRepository.save(menu);
    }

    @Transactional
    public Menu removeItemFromMenu(Long userId, Long productId) {
        Optional<Menu> optionalMenu = menuRepository.findByUserId(userId);
        if (optionalMenu.isPresent()) {
            Menu menu = optionalMenu.get();
            menu.getItems().removeIf(item -> item.getProductId().equals(productId));
            return menuRepository.save(menu);
        }
        return null;
    }

    public Menu getMenu(Long userId){
        return menuRepository.findByUserId(userId).orElse(null);
    }
}
