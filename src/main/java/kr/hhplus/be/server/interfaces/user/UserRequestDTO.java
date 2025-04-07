package kr.hhplus.be.server.interfaces.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserRequestDTO(
) {
    public record ChargeUserPointRequest(
            @NotNull(message = "사용자 ID는 필수입니다.")
            Long userId,

            @Positive(message = "충전 금액은 0보다 커야 합니다.")
            long amount
    ) {
    }
}
