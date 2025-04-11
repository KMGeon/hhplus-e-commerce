package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;


public record OrderInfo (
        Long orderId,
        String status,
        Long totalPrice,
        Long totalEa,
        LocalDateTime expireTime
){
    public static OrderInfo from(OrderEntity order) {
        return new OrderInfo(
                order.getId(),
                order.getStatus().getDescription(),
                order.getTotalPrice(),
                order.getTotalEa(),
                order.getExpireTime()
        );
    }
}
