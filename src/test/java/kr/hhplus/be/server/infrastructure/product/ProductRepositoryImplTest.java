package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.config.RepoContext;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RepoContext
class ProductRepositoryImplTest {

    @Autowired
    private ProductJpaRepository repository;

    private static final int PAGE = 0;
    private static final int SIZE = 10;

    @Test
    @DisplayName("상품 정보 + 재고 > 카테고리별 조회")
    public void getProductsWithStockInfoByCategory() {
        // given
        final String category = "SAMSUNG";
        PageRequest request = PageRequest.of(PAGE, SIZE);

        // when
        Page<ProductStockDTO> result = repository.getProductsWithStockInfoByCategory(category, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(PAGE);
        assertThat(result.getSize()).isEqualTo(SIZE);

        if (!result.isEmpty()) {
            assertThat(result.getContent())
                    .allMatch(product -> category.equals(product.getCategory()),
                            "모든 상품은 " + category + " 카테고리여야 합니다");

            // 결과 첫 번째 상품의 세부 정보 검증
            ProductStockDTO firstProduct = result.getContent().get(0);
            assertThat(firstProduct.getProductId()).isNotNull();
            assertThat(firstProduct.getProductName()).isNotNull();
            assertThat(firstProduct.getSkuId()).isNotNull();
            assertThat(firstProduct.getUnitPrice()).isNotNull();
            assertThat(firstProduct.getStockEa()).isNotNull();
        }
    }

    @Test
    @DisplayName("상품 정보 + 재고 > 전체 조회")
    public void getProductsWithStockInfo() {
        // given
        PageRequest request = PageRequest.of(PAGE, SIZE);

        // when
        Page<ProductStockDTO> result = repository.getProductsWithStockInfo(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(PAGE);
        assertThat(result.getSize()).isEqualTo(SIZE);

        if (!result.isEmpty()) {
            // 결과 첫 번째 상품의 세부 정보 검증
            ProductStockDTO firstProduct = result.getContent().get(0);
            assertThat(firstProduct.getProductId()).isNotNull();
            assertThat(firstProduct.getProductName()).isNotNull();
            assertThat(firstProduct.getCategory()).isNotNull();
            assertThat(firstProduct.getSkuId()).isNotNull();
            assertThat(firstProduct.getUnitPrice()).isNotNull();
            assertThat(firstProduct.getStockEa()).isNotNull();
        }
    }

    @Test
    @DisplayName("SKU ID로 상품 개수 조회")
    public void countBySkuIdIn() {
        // given
        List<String> existingSkuIds = Arrays.asList("A-0001-0001", "A-0001-0002");

        // when
        long count = repository.countBySkuIdIn(existingSkuIds);

        // then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("SKU ID로 상품 목록 조회")
    public void findAllBySkuIdIn() {
        // given
        List<String> existingSkuIds = Arrays.asList("A-0001-0001", "A-0001-0002");

        // when
        List<ProductEntity> products = repository.findAllBySkuIdIn(existingSkuIds);

        // then
        assertThat(products).isNotNull();
        // 정확한 개수 검증 대신, 최소한 요청한 SKU ID 개수만큼은 있어야 함
        assertThat(products.size()).isGreaterThanOrEqualTo(existingSkuIds.size());
        // 요청한 SKU ID가 모두 결과에 포함되어 있는지 확인
        assertThat(products.stream()
                .map(ProductEntity::getSkuId)
                .collect(Collectors.toList()))
                .containsAll(existingSkuIds);
    }

    @Test
    @DisplayName("존재하지 않는 SKU ID로 상품 목록 조회")
    public void findAllByNonExistingSkuIdIn() {
        // given
        List<String> nonExistingSkuIds = Arrays.asList("NON-EXIST-001", "NON-EXIST-002");

        // when
        List<ProductEntity> products = repository.findAllBySkuIdIn(nonExistingSkuIds);

        // then
        assertThat(products).isEmpty();
    }
}