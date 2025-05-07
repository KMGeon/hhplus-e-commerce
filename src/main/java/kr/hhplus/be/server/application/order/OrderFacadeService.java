package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.stock.StockCommand;
import kr.hhplus.be.server.domain.stock.StockInfo;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserInfo;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.stock.StockInfo.Stock.toOrderCommand;

@Service
@RequiredArgsConstructor
public class OrderFacadeService {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final StockService stockService;

    @Transactional
    public Long createOrder(OrderCriteria.Order criteria) {
        UserInfo.User getUser = userService.getUser(criteria.userId());

        OrderCriteria.Item[] items = OrderCriteria.Item.toArray(criteria.products());
        productService.checkProductSkuIds(items);

        StockCommand.Order stockCommand = criteria.toStockCommand();
        List<StockInfo.Stock> getStocks = stockService.checkEaAndProductInfo(stockCommand);
        Long createOrderId = orderService.createOrder(getUser.userId(), toOrderCommand(getStocks, stockCommand));

         stockService.decreaseStockLock(createOrderId, stockCommand);
         return createOrderId;
    }

    public List<Long> republishUnpublishedEvents(){
        List<Long> expireIds = orderService.updateExpireOrderStatus();
        stockService.restoreStock(expireIds);
        return expireIds;
    }
}