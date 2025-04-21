package com.example.shoppingcart.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("cart")      // key 形如 cart:1
@Data
public class Cart {

    @Id
    private String id;   // 可使用 userId 字符串，也能用 UUID

    @Indexed
    private Long userId;
    private List<CartItem> items = new ArrayList<>();

    @TimeToLive
    private Long ttl = 604800L; // 单位：秒，等于 7 天

    public void addItem(CartItem item){
        items.add(item);
    }
    public void removeItem(Long productId){
        items.removeIf(i -> i.getProductId().equals(productId));
    }
}
