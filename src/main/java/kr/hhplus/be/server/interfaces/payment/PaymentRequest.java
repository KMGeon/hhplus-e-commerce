package kr.hhplus.be.server.interfaces.payment;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.application.payment.PaymentCriteria;

public class PaymentRequest {

    public record PayRequest(
            @NotNull(message = "사용자 ID는 필수입니다.")
            Long userId,

            @NotNull(message = "주문 ID는 필수입니다.")
            Long orderId,

            Long userCouponId
    ){
        public PaymentCriteria.Pay toCriteria() {
            return new PaymentCriteria.Pay(
                    this.userId(),
                    this.orderId(),
                    this.userCouponId()
            );
        }
    }
}
