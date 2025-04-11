package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum CategoryEnum {
    APPLE("A", "애플"),
    SAMSUNG("S", "삼성"),
    LG("G", "엘지"),
    SONY("Y", "소니"),
    DELL("D", "델");

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
        if (categoryEnum == null) {
            throw new IllegalArgumentException("카테고리 Enum이 null입니다.");
        }

        return categoryEnum.getCategoryCode();
    }
}