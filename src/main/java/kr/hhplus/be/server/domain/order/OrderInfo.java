package kr.hhplus.be.server.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class OrderInfo {
    public record OrderCreateInfo(
            Long orderId,
            String status,
            Long totalPrice,
            Long totalEa,
            LocalDateTime expireTime
    ) {
        public static OrderInfo.OrderCreateInfo from(OrderEntity order) {
            return null;
        }
    }

    public record OrderPaymentInfo(Long orderId, BigDecimal totalPrice){
        public static OrderInfo.OrderPaymentInfo from(OrderEntity order) {
            return new OrderInfo.OrderPaymentInfo(
                    order.getId(),
                    order.getTotalPrice()
            );
        }
    }

}