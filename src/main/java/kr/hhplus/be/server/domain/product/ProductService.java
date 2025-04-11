package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public List<ProductInfo.ProductInfoResponse> getProductByCategoryCode(String categoryCode) {
        return productRepository.findProductWithStockByCategoryCode(categoryCode);
    }

    public List<ProductInfo.ProductInfoResponse> getAllProduct() {
        return productRepository.findProductWithStock();
    }

    public boolean validateProducts(List<OrderCommand.Item> items) {
        Set<Long> productIds = items.stream()
                .map(OrderCommand.Item::productId)
                .collect(Collectors.toSet());

        List<ProductEntity> products = productRepository.findAllByIdIn(new ArrayList<>(productIds));

        return products.size() == productIds.size();
    }

    public boolean checkStockAvailability(List<OrderCommand.Item> items) {
        List<Long> productIds = getCommandProductIds(items);

        List<StockEntity> stocks = stockRepository.findAllByProductIdIn(productIds);

        Map<Long, StockEntity> stockMap = stocks.stream()
                .collect(Collectors.toMap(
                        stock -> stock.getProductEntity().getId(),
                        stock -> stock
                ));

        return items.stream().allMatch(item -> {
            StockEntity stock = stockMap.get(item.productId());
            return stock != null && stock.getEa() >= item.ea();
        });
    }

    public void decreaseStock(OrderCommand.Order command) {
        List<Long> productIds = getCommandProductIds(command.items());

        List<StockEntity> stocks = stockRepository.findAllByProductIdIn(productIds);
        Map<Long, StockEntity> stockMap = stocks.stream()
                .collect(Collectors.toMap(
                        stock -> stock.getProductEntity().getId(),
                        stock -> stock
                ));

        for (OrderCommand.Item item : command.items()) {
            StockEntity stock = stockMap.get(item.productId());
            if (stock != null) stock.decreaseEa(item.ea());
        }
    }

    private static List<Long> getCommandProductIds(List<OrderCommand.Item> items) {
        List<Long> productIds = items.stream()
                .map(OrderCommand.Item::productId)
                .collect(Collectors.toList());
        return productIds;
    }
}