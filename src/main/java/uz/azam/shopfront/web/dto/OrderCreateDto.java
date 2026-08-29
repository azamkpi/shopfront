package uz.azam.shopfront.web.dto;

import java.util.List;

public record OrderCreateDto(
        Long variantId,
        String fullName,
        String phone,
        String note,
        List<Line> items
) {
    public record Line(Long variantId, Integer qty){}
}
