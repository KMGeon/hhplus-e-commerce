package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    CONFIRMED("주문완료"),
    PAID("결제완료"),
    CANCELLED("취소");
    private final String description;
}