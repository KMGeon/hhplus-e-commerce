package kr.hhplus.be.server.interfaces.order;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.interfaces.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacadeService orderFacadeService;

    @PostMapping("/order")
    public ApiResponse<Integer> createOrder(
            @Valid @RequestBody OrderRequestDTO.CreateOrderRequest request
    ) {
        OrderCriteria.Order criteria = request.toCriteria();
        OrderInfo order = orderFacadeService.createOrder(criteria);
        return ApiResponse.maskToInteger(OrderResult.OrderCreate.from(order));
    }
}
