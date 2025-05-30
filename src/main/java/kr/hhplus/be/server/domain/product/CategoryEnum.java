package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum CategoryEnum {
    APPLE("APPLE", "애플"),
    SAMSUNG("SAMSUNG", "삼성"),
    LG("LG", "엘지"),
    SONY("SONY", "소니"),
    DELL("DELL", "델");

    private String categoryCode;
    private String description;

    public static Optional<CategoryEnum> validateCategoryCode(String categoryCode) {
        if (categoryCode == null || categoryCode.isEmpty())
            return Optional.empty();

        return Arrays.stream(CategoryEnum.values())
                .filter(category -> category.getCategoryCode().equals(categoryCode))
                .findFirst();
    }

    public static String getCategoryCode(CategoryEnum categoryEnum) {
        if (categoryEnum == null)
            throw new IllegalArgumentException("카테고리 Enum이 null입니다.");
        return categoryEnum.getCategoryCode();
    }

    public static CategoryEnum fromCategoryCode(String categoryCode) {
        if (categoryCode == null || categoryCode.isEmpty()) {
            throw new IllegalArgumentException("카테고리 코드가 null이거나 비어있습니다.");
        }
        
        return validateCategoryCode(categoryCode)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 카테고리가 없습니다: " + categoryCode));
    }
}