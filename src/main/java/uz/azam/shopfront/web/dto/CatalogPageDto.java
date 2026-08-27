package uz.azam.shopfront.web.dto;

import java.util.List;

public record CatalogPageDto(
        List<ProductCardDto> items,
        int page,
        boolean hasNext,
        long total
) { }
