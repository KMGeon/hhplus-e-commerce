package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductStockDTO> getProductByCategoryCode(String categoryCode) {
        return productRepository.getProductsWithStockInfoByCategory(categoryCode);
    }

    public List<ProductStockDTO> getAllProduct() {
        return productRepository.getProductsWithStockInfo();
    }


    public void checkProductSkuIds(OrderCriteria.Item... items) {
        List<String> skuIds = Arrays.stream(items)
                .map(OrderCriteria.Item::skuId)
                .toList();
        long count = productRepository.countBySkuIdIn(skuIds);
        if (count != skuIds.size()) throw new RuntimeException("잘못된 SKU ID가 포함되어 있습니다.");
    }
}