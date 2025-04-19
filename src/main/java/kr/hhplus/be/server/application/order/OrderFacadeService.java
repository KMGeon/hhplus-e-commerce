package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.stock.StockCommand;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacadeService {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final StockService stockService;

    @Transactional
    public int createOrder(OrderCriteria.Order criteria) {
        Long requestUserId = criteria.userId();

        userService.getUserId(requestUserId);

        // 상품 ID 검증
        List<String> skuIds = criteria.products().stream()
                .map(item -> item.skuId())
                .toList();

        StockCommand.Order stockCommand = criteria.toStockCommand();
        productService.validateAllSkuIds(skuIds);

        // 재고 검증
        stockService.isEnoughStock(stockCommand);


        // 주문 생성
        long createOrderId = orderService.createOrder(criteria.toCommand());

        // 재고감소
        return stockService.decreaseStock(createOrderId, stockCommand);
    }
}