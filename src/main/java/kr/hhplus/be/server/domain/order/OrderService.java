package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderCoreRepository orderCoreRepository;
    private final ProductRepository productRepository;


    @Transactional
    public long createOrder(OrderCommand.Order requestCommand){
        OrderEntity order = OrderEntity.createOrder(requestCommand.userId(), LocalDateTime.now());
        order.orderStatusConfirm();

        List<String> skuIds = requestCommand.items().stream()
                .map(OrderCommand.Item::skuId)
                .toList();

        Map<String, ProductEntity> productMap = productRepository.findAllBySkuIdIn(skuIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductEntity::getSkuId,
                        product -> product
                ));

        List<OrderItemEntity> orderItems = requestCommand.items().stream()
                .map(item -> {
                    ProductEntity product = productMap.get(item.skuId());
                    return OrderItemEntity.createOrderItem(product, item.ea());
                })
                .toList();

        order.addOrderItems(orderItems);
        OrderEntity createOrder = orderCoreRepository.save(order);
        return createOrder.getId();
    }

    public OrderInfo.OrderPaymentInfo isAvailableOrder(long orderId) {
        OrderEntity getOrder = orderCoreRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));

        getOrder.isAvailablePaymentState();

        return OrderInfo.OrderPaymentInfo.from(getOrder);
    }

    public void setDiscountAmount(Long orderId, BigDecimal discountAmount) {
        orderCoreRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."))
                .setDiscountAmount(discountAmount);
    }
}