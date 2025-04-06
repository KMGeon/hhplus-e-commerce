package kr.hhplus.be.server.controller.order;

import jakarta.validation.Valid;
import kr.hhplus.be.server.config.common.ApiResponse;
import kr.hhplus.be.server.controller.order.dto.request.CreateOrderRequest;
import kr.hhplus.be.server.controller.order.dto.request.PaymentRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    @PostMapping("/order")
    public ApiResponse<Integer> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ApiResponse.maskToInteger(1);
    }

    @PostMapping("/payment")
    public ApiResponse<Integer> payment(
            @Valid @RequestBody PaymentRequest request
    ) {
        return ApiResponse.maskToInteger(1);
    }

}
