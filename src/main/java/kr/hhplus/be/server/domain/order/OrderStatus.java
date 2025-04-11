package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING("대기"),
    SUCCESS("완료"),
    CANCELLED("취소");

    private String description;
}