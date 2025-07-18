package com.example.menu.repository;

import com.example.menu.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByUserId(Long userId);
}
