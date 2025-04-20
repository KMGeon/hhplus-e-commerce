package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CategoryEnumTest {

    @Test
    @DisplayName("카테고리 Enum 값 검증")
    void testCategoryEnumValues() {
        // given & when & then
        assertEquals("APPLE", CategoryEnum.APPLE.getCategoryCode());
        assertEquals("애플", CategoryEnum.APPLE.getDescription());

        assertEquals("SAMSUNG", CategoryEnum.SAMSUNG.getCategoryCode());
        assertEquals("삼성", CategoryEnum.SAMSUNG.getDescription());

        assertEquals("LG", CategoryEnum.LG.getCategoryCode());
        assertEquals("엘지", CategoryEnum.LG.getDescription());

        assertEquals("SONY", CategoryEnum.SONY.getCategoryCode());
        assertEquals("소니", CategoryEnum.SONY.getDescription());

        assertEquals("DELL", CategoryEnum.DELL.getCategoryCode());
        assertEquals("델", CategoryEnum.DELL.getDescription());
    }

    @Test
    @DisplayName("유효한 카테고리 코드로 검증 시 해당 Enum 반환")
    void validateCategoryCode_withValidCode_returnsEnum() {
        // given
        String validCode = "APPLE";

        // when
        Optional<CategoryEnum> result = CategoryEnum.validateCategoryCode(validCode);

        // then
        assertTrue(result.isPresent());
        assertEquals(CategoryEnum.APPLE, result.get());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("null 또는 빈 문자열 카테고리 코드로 검증 시 빈 Optional 반환")
    void validateCategoryCode_withNullOrEmpty_returnsEmptyOptional(String invalidCode) {
        // when
        Optional<CategoryEnum> result = CategoryEnum.validateCategoryCode(invalidCode);

        // then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 코드로 검증 시 빈 Optional 반환")
    void validateCategoryCode_withNonExistentCode_returnsEmptyOptional() {
        // given
        String nonExistentCode = "INVALID_CODE";

        // when
        Optional<CategoryEnum> result = CategoryEnum.validateCategoryCode(nonExistentCode);

        // then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("카테고리 Enum으로 카테고리 코드 조회")
    void getCategoryCode_withValidEnum_returnsCode() {
        // given
        CategoryEnum category = CategoryEnum.SAMSUNG;

        // when
        String code = CategoryEnum.getCategoryCode(category);

        // then
        assertEquals("SAMSUNG", code);
    }

    @Test
    @DisplayName("null Enum으로 카테고리 코드 조회 시 예외 발생")
    void getCategoryCode_withNullEnum_throwsException() {
        // given
        CategoryEnum nullCategory = null;

        // when
// then
        assertThrows(IllegalArgumentException.class, () -> CategoryEnum.getCategoryCode(nullCategory));
    }
}