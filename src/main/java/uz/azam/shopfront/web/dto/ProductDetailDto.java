package uz.azam.shopfront.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductDetailDto(
        Long id,
        String name,
        String description,
        boolean negotiable,
        List<String> images,
        List<VariantDto> variants
) {
    public record VariantDto(
            Long id,
            Map<String, String> attributes,
            BigDecimal price,
            BigDecimal oldPrice,
            boolean inStock
    ) {}
}
