package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.DatePathProvider;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.domain.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;


    @Transactional(readOnly = true)
    public List<ProductStockDTO> getProductByCategoryCode(String categoryCode) {
        return productRepository.getProductsWithStockInfoByCategory(categoryCode);
    }

    @Transactional(readOnly = true)
    public List<ProductStockDTO> getAllProduct() {
        return productRepository.getProductsWithStockInfo();
    }

    public void validateAllSkuIds(List<String> skuIds) {
        long count = productRepository.countBySkuIdIn(skuIds);

        if (count != skuIds.size()) throw new RuntimeException("잘못된 SKU ID가 포함되어 있습니다.");
    }

    public void getHotProducts() {
        LocalDateTime current = LocalDateTime.now();

        LocalDateTime startOfDay = current.minusDays(3).with(LocalTime.MIN);
        LocalDateTime endOfDay = current.with(LocalTime.MAX);


        String startPath = DatePathProvider.toPath(startOfDay);
        String endPath = DatePathProvider.toPath(endOfDay);

    }
}