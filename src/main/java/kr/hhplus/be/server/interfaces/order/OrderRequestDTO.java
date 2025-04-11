package kr.hhplus.be.server.interfaces.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.order.PaymentCommand;

import java.util.List;

public record OrderRequestDTO (){
    public record CreateOrderRequest(
            @NotNull(message = "사용자 ID는 필수 값입니다. 올바른 사용자 ID를 입력해주세요.")
            Long userId,

            @Valid
            @NotEmpty(message = "상품은 최소 1개를 선택하세요")
            List<OrderProductRequest> products
    ) {
        public OrderCriteria.Order toCriteria() {
            return new OrderCriteria.Order(
                    this.userId,
                    this.products().stream()
                            .map(orderProductRequest -> orderProductRequest.toCriteria())
                            .toList()
            );
        }
    }

    public record OrderProductRequest(
            @NotNull(message = "상품 ID는 필수 값입니다. 올바른 상품 ID를 입력해주세요.")
            Long productId,

            @NotNull(message = "상품 구매 수량은 필수 값입니다.")
            @Positive(message = "상품 구매 수량은 1개 이상이어야 합니다.")
            long ea,

            @Positive(message = "가격은 양수입니다.")
            long price

    ) {
        public OrderCriteria.Item toCriteria() {
            return new OrderCriteria.Item(
                    this.productId,
                    this.ea,
                    this.price
            );
        }
    }
}
