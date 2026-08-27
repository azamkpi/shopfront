package uz.azam.shopfront.web.dto;

public record OrderCreateDto(
        Long variantId,
        String fullName,
        String phone,
        String note
) {
}
