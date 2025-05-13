package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable(
            value = "product",
            key = "@cacheKeyManager.generateKey(#page, #size)",
            condition = "#page == 0"
    )
    public ProductInfo.CustomPageImpl<ProductStockDTO> getAllProduct(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductStockDTO> originalPage = productRepository.getProductsWithStockInfo(pageable);
        return new ProductInfo.CustomPageImpl<>(originalPage);
    }

    @Cacheable(
            value = "product",
            key = "@cacheKeyManager.generateKey(#categoryCode, #page, #size)",
            condition = "#page == 0"
    )
    public ProductInfo.CustomPageImpl<ProductStockDTO> getProductByCategoryCode(String categoryCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductStockDTO> originalPage = productRepository.getProductsWithStockInfoByCategory(categoryCode, pageable);
        return new ProductInfo.CustomPageImpl<>(originalPage);
    }


    public void checkProductSkuIds(OrderCriteria.Item... items) {
        List<String> skuIds = Arrays.stream(items)
                .map(OrderCriteria.Item::skuId)
                .toList();
        long count = productRepository.countBySkuIdIn(skuIds);
        if (count != skuIds.size()) throw new RuntimeException("잘못된 SKU ID가 포함되어 있습니다.");
    }
}