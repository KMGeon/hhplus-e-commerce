package kr.hhplus.be.server.interfaces.user;

import jakarta.validation.constraints.Positive;

public record UserRequestDTO(
) {
    public record ChargeUserPointRequest(
            Long userId,

            @Positive(message = "충전 금액은 0보다 커야 합니다.")
            long amount
    ) {
    }
}
