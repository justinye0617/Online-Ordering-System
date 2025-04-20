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
    public Menu addItemToMenu(Long userId, String name, Double price, String imageUrl) {
        Menu menu = menuRepository.findByUserId(userId).orElseGet(() -> {
            Menu m = new Menu(); m.setUserId(userId); return menuRepository.save(m);
        });

        boolean exists = false;
        for (MenuItem item : menu.getItems()) {
            if (item.getName().equalsIgnoreCase(name)) {      // 按菜名更新
                item.setPrice(price);
                item.setImageUrl(imageUrl);
                exists = true;
                break;
            }
        }
        if (!exists) {                                        // 新建
            MenuItem mi = new MenuItem();
            mi.setName(name);
            mi.setPrice(price);
            mi.setImageUrl(imageUrl);
            menu.addItem(mi);
        }
        return menuRepository.save(menu);
    }


    @Transactional
    public Menu removeItemFromMenu(Long userId, Long productId) {
        Optional<Menu> optionalMenu = menuRepository.findByUserId(userId);
        if (optionalMenu.isPresent()) {
            Menu menu = optionalMenu.get();
            menu.getItems().removeIf(item -> item.getId().equals(productId));
            return menuRepository.save(menu);
        }
        return null;
    }

    public Menu getMenu(Long userId){
        Menu menu = menuRepository.findByUserId(userId).orElseGet(() -> {
            Menu m = new Menu(); m.setUserId(userId); return menuRepository.save(m);
        });
        return menu;
    }
}
