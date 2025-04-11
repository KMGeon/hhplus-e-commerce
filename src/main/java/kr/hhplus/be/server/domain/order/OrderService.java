package kr.hhplus.be.server.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderCoreRepository orderCodeRepository;

    @Transactional
    public OrderInfo createOrder(OrderCommand.Order command, LocalDateTime now) {
        OrderEntity newOrder = OrderEntity.createOrder(
                command.userId(),
                command.calculateTotalPrice(),
                now,
                command.calculateTotalQuantity()
        );
        orderCodeRepository.save(newOrder);

        for (OrderCommand.Item item : command.items()) {
            OrderItemEntity orderItem = OrderItemEntity.createOrderItem(
                    item.productId(),
                    item.ea(),
                    item.price()
            );
            orderItem.setOrder(newOrder);
            orderCodeRepository.save(orderItem);
        }

        return OrderInfo.from(newOrder);
    }

    public boolean isValidOrder(long orderId){
        return orderCodeRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("주문이 존재하지 않습니다."))
                .isExpired();
    }

}