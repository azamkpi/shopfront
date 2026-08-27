package uz.azam.shopfront.web.dto;

import java.util.List;

public record ShopInfoDto(
        String title,
        String welcomeText,
        String currency,
        String phone,
        List<CategoryDto> categories
) {
    public record CategoryDto(Long id, String name, String slug) {

    }
}
