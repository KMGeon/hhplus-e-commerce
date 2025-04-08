package kr.hhplus.be.server.infrastructure.product;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.stock.StockEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FakeProductRepository implements ProductRepository {
    private final Map<Long, ProductEntity> productStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 상품 1: 맥북 프로
        ProductEntity product1 = new ProductEntity(
                1L,
                "SKU123",
                'A',
                "맥북 프로",
                2500000L
        );
        StockEntity stock1 = new StockEntity(
                1L,
                "SKU123",
                10
        );
        product1.setStock(stock1);

        // 상품 2: 에어팟 프로
        ProductEntity product2 = new ProductEntity(
                2L,
                "SKU456",
                'B',
                "에어팟 프로",
                350000L
        );
        StockEntity stock2 = new StockEntity(
                2L,
                "SKU456",
                5
        );
        product2.setStock(stock2);

        productStore.put(product1.getId(), product1);
        productStore.put(product2.getId(), product2);
    }



    @Override
    public Optional<ProductEntity> findById(Long id) {
        return Optional.ofNullable(productStore.get(id));
    }

    @Override
    public List<ProductEntity> findAll() {
        return new ArrayList<>(productStore.values());
    }

    @Override
    public List<ProductEntity> findAllByCategory(char category) {
        return productStore.values().stream()
                .filter(product -> product.getCategory() == category)
                .toList();
    }



}
