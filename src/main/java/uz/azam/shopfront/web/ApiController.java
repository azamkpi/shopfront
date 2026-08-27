package uz.azam.shopfront.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.azam.shopfront.domain.Shop;
import uz.azam.shopfront.repo.CategoryRepository;
import uz.azam.shopfront.service.CatalogService;
import uz.azam.shopfront.service.OrderService;
import uz.azam.shopfront.service.ShopContext;
import uz.azam.shopfront.web.dto.CatalogPageDto;
import uz.azam.shopfront.web.dto.OrderCreateDto;
import uz.azam.shopfront.web.dto.ProductDetailDto;
import uz.azam.shopfront.web.dto.ShopInfoDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final CatalogService catalogService;
    private final OrderService orderService;
    private final ShopContext shopContext;
    private final CategoryRepository categoryRepository;

    @GetMapping("/shop")
    public ShopInfoDto shop() {
        Shop shop = shopContext.shop();
        List<ShopInfoDto.CategoryDto> categories = categoryRepository
                .findByShopIdOrderBySortOrderAsc(shop.getId())
                .stream()
                .map(c -> new ShopInfoDto.CategoryDto(c.getId(), c.getName(), c.getSlug()))
                .toList();

        return new ShopInfoDto(shop.getTitle(), shop.getWelcomeText(),
                shop.getCurrency(), shop.getPhone(), categories);
    }

    @GetMapping("/products")
    public CatalogPageDto products(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return catalogService.catalog(categoryId, page, Math.min(size, 50));
    }

    @GetMapping("/products/{id}")
    public ProductDetailDto product(@PathVariable Long id) {
        return catalogService.detail(id);
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody OrderCreateDto dto,
            @RequestHeader(value="X-Telegram-User-Id", required = false) Long tgUserId
    ) {
        var order = orderService.create(dto, null);
        return ResponseEntity.ok(Map.of("id", order.getId(), "status", "ok"));
    }
}
