package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.projection.HotProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderCoreRepository orderCoreRepository;

    public Long createOrder(Long userId, List<OrderCommand.Product> products) {
        OrderEntity order = OrderEntity.createOrder(userId, LocalDateTime.now());

        List<OrderItemEntity> orderItems = products.stream()
                .map(v1 -> OrderItemEntity.createOrderItem(v1.skuId(), v1.ea(), v1.unitPrice()))
                .toList();
        order.addOrderItems(orderItems);

        OrderEntity createOrder = orderCoreRepository.save(order);
        return createOrder.getId();
    }

    public OrderInfo.OrderPaymentInfo isAvailableOrder(long orderId) {
        OrderEntity getOrder = orderCoreRepository.findById(orderId);
        getOrder.isAvailablePaymentState();
        return OrderInfo.OrderPaymentInfo.from(getOrder);
    }

    public void restoreOrderStatusCancel(Long orderId) {
        orderCoreRepository.findById(orderId).cancel();
    }


    public BigDecimal applyToDisCount(Long orderId, BigDecimal discountAmount) {
        OrderEntity order = orderCoreRepository.findById(orderId);
        order.applyDiscount(discountAmount);
        order.complete();
        return order.getFinalAmount();
    }

    public List<HotProductDTO> getHotProducts() {
        LocalDateTime current = LocalDateTime.now();

        LocalDateTime startOfDay = current.minusDays(3).with(LocalTime.MIN);
        LocalDateTime endOfDay = current.with(LocalTime.MAX);

        String startPath = DatePathProvider.toPath(startOfDay);
        String endPath = DatePathProvider.toPath(endOfDay);

        return orderCoreRepository.findHotProducts(startPath, endPath);
    }

    /**
     * 주문 만료 상태 업데이트
     */
    public void updateExpireOrderStatus() {
        orderCoreRepository.updateExpireOrderStatus();
    }
}