package uz.azam.shopfront.web.dto;

import java.math.BigDecimal;

public record ProductCardDto(
        Long id,
        Long variantId,
        String name,
        BigDecimal price,
        boolean negotiable,
        boolean inStock,
        String imageUrl
) { }
