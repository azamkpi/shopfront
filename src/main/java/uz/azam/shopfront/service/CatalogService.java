package uz.azam.shopfront.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.azam.shopfront.domain.Product;
import uz.azam.shopfront.domain.ProductVariant;
import uz.azam.shopfront.repo.ProductRepository;
import uz.azam.shopfront.web.dto.CatalogPageDto;
import uz.azam.shopfront.web.dto.ProductCardDto;
import uz.azam.shopfront.web.dto.ProductDetailDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ProductRepository productRepository;
    private final ShopContext shopContext;

    @Transactional(readOnly = true)
    public CatalogPageDto catalog(Long categoryId, int page, int size) {
        Long shopId = shopContext.shopId();
        var pageable = PageRequest.of(page, size);

        Page<Product> result = (categoryId == null)
                ? productRepository.findByShopIdAndActiveTrueOrderByCreatedAtDesc(shopId, pageable)
                : productRepository.findByShopIdAndCategoryIdAndActiveTrueOrderByCreatedAtDesc(shopId, categoryId, pageable);

        List<ProductCardDto> items = result.getContent().stream()
                .map(this::toCard)
                .toList();

        return new CatalogPageDto(items, page, result.hasNext(), result.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProductDetailDto detail(Long id) {
        Product p = productRepository.findByIdAndShopId(id, shopContext.shopId())
                .orElseThrow(() -> new IllegalArgumentException("Mahsulot topilmadi"));

        p.setViews(p.getViews() + 1);

        List<String> images = p.getImages().stream()
                .map(img -> "/img/" + img.getFileId())
                .toList();

        List<ProductDetailDto.VariantDto> variants = p.getVariants().stream()
                .map(v -> new ProductDetailDto.VariantDto(
                        v.getId(), v.getAttributes(), v.getPrice(),
                        v.getOldPrice(), v.getInStock()))
                .toList();

        return new ProductDetailDto(p.getId(), p.getName(), p.getDescription(),
                p.getNegotiable(), images, variants);
    }

    private ProductCardDto toCard(Product p) {
        ProductVariant first = p.getVariants().isEmpty() ? null : p.getVariants().get(0);
        String imageUrl = p.getImages().isEmpty()
                ? null
                : "/img/" + p.getImages().get(0).getFileId();

        return new ProductCardDto(
                p.getId(),
                first != null ? first.getId() : null,
                p.getName(),
                first != null ? first.getPrice() : null,
                p.getNegotiable(),
                first == null || first.getInStock(),
                imageUrl
        );
    }
}
