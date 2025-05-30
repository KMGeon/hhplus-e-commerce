package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.vo.RankingItem;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static kr.hhplus.be.server.support.CacheKeyManager.CacheKeyName.HOT_PRODUCT_QUERYDSL;

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

    public void addRankingSystemProducts(Long orderId) {
        orderCoreRepository.findOrderItemsWithProductInfo(orderId).forEach(v1 -> orderCoreRepository.addDailySummeryRanking(
                DatePathProvider.toPath(LocalDateTime.now()),
                RankingItem.create(v1.getSkuId(), v1.getProductName()),
                v1.getEa()
        ));
    }


    @Cacheable(
            value = HOT_PRODUCT_QUERYDSL,
            key = "@cacheKeyManager.generateKey(#datePath)"
    )
    public List<kr.hhplus.be.server.domain.order.projection.HotProductQuery> getHotProducts(String datePath) {
        LocalDateTime currentDate = DatePathProvider.toDateTime(datePath);
        return orderCoreRepository.findHotProducts(
                DatePathProvider.toPath(currentDate.minusDays(3).with(LocalTime.MIN)),
                DatePathProvider.toPath(currentDate.with(LocalTime.MAX))
        );
    }

    public List<Long> updateExpireOrderStatus() {
        List<Long> expiredOrderIds = orderCoreRepository.findExpiredOrderIds();
        orderCoreRepository.updateExpireOrderStatus(expiredOrderIds);
        return expiredOrderIds;
    }
}