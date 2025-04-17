package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.PaymentCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderFacadeService {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    @Transactional
    public OrderInfo createOrder(OrderCriteria.Order criteria) {
        // 1. 유저 검증
        OrderCommand.Order orderCommand = criteria.toCommand();
        userService.isValidUser(orderCommand.userId());

        // 2. 상품 검증
        if (!productService.validateProducts(orderCommand.items()))
            throw new IllegalArgumentException("없는 상품 ID가 존재합니다.");

        // 3. 재고 검증
        if (!productService.checkStockAvailability(orderCommand.items()))
            throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");

        // 4. 주문 생성
        OrderInfo order = orderService.createOrder(orderCommand, LocalDateTime.now());
        productService.decreaseStock(orderCommand);
        return order;
    }
}