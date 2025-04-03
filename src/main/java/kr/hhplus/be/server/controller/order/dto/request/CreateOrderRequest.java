package kr.hhplus.be.server.controller.order.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "사용자 ID는 필수 값입니다. 올바른 사용자 ID를 입력해주세요.")
        Long userId,

        @Nullable
        Long couponId,

        @Valid
        @NotEmpty(message = "상품은 최소 1개를 선택하세요")
        List<OrderProductRequest> products
) {
    public static CreateOrderRequest of(
            Long userId,
            Long couponId,
            List<OrderProductRequest> products
    ) {
        return new CreateOrderRequest(userId, couponId, products);
    }

    public record OrderProductRequest(
            @NotNull(message = "상품 ID는 필수 값입니다. 올바른 상품 ID를 입력해주세요.")
            Long productId,

            @NotNull(message = "상품 구매 수량은 필수 값입니다.")
            @Positive(message = "상품 구매 수량은 1개 이상이어야 합니다.")
            Integer quantity
    ) {
        public static OrderProductRequest of(Long id, Integer quantity) {
            return new OrderProductRequest(id, quantity);
        }
    }
}