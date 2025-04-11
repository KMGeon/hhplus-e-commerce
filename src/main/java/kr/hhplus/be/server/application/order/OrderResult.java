package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderInfo;

import java.time.LocalDateTime;

public class OrderResult {
    public record OrderCreate(
            Long orderId,
            String status,
            Long totalPrice,
            Long totalEa,
            LocalDateTime expireTime

    ){
        public static OrderResult.OrderCreate from(OrderInfo info) {
            return new OrderResult.OrderCreate(
                    info.orderId(),
                    info.status(),
                    info.totalPrice(),
                    info.totalEa(),
                    info.expireTime()
            );
        }
    }
}
