package kr.hhplus.be.server.interfaces.order.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotBlank(message = "사용자 ID는 필수입니다.")
        String userId,

        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,

        String couponId
) {}