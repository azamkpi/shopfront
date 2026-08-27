package uz.azam.shopfront.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.azam.shopfront.domain.Shop;
import uz.azam.shopfront.repo.CategoryRepository;
import uz.azam.shopfront.service.ShopContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final ShopContext shopContext;
    private final CategoryRepository categoryRepository;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Shop shop = shopContext.shop();
        List<String> categories = categoryRepository
                .findByShopIdOrderBySortOrderAsc(shop.getId())
                .stream()
                .map(category -> category.getName())
                .toList();

        return Map.of(
               "status", "UP",
               "shop", shop.getTitle(),
               "slug", shop.getSlug(),
               "categories", categories
        );
    }
}
