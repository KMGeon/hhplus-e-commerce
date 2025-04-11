package kr.hhplus.be.server.interfaces.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.order.PaymentCommand;

public class PaymentRequestDTO {
    public record PayRequest(
            @NotBlank(message = "사용자 ID는 필수입니다.")
            Long userId,

            @NotNull(message = "주문 ID는 필수입니다.")
            Long orderId,

            String couponId
    ){
        public PaymentCommand toCommand() {
            return new PaymentCommand(this.userId, this.orderId, this.couponId);
        }
    }
}
