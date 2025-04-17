package kr.hhplus.be.server.domain.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentCommand(
        @NotBlank(message = "사용자 ID는 필수입니다.")
        Long userId,
        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,
        Long couponId
){
}