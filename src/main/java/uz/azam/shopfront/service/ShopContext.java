package uz.azam.shopfront.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.azam.shopfront.domain.Shop;
import uz.azam.shopfront.repo.ShopRepository;

@Component
@RequiredArgsConstructor
public class ShopContext {

    private final ShopRepository shopRepository;

    @Value("${app.shop-slug}")
    private String shopSlug;

    private volatile Shop cached;

    public Shop shop() {
        if (cached == null) {
            cached = shopRepository.findBySlug(shopSlug)
                    .orElseThrow(() -> new IllegalStateException("Shop topilmadi: " + shopSlug));
        }
        return cached;
    }

    public Long shopId() {
        return shop().getId();
    }

    public void refresh() {
        cached = null;
    }
}
