package kr.hhplus.be.server.interfaces.order;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final OrderFacadeService orderFacadeService;

    @PostMapping("/order")
    public ApiResponse<Long> createOrder(
            @Valid @RequestBody OrderRequestDTO.CreateOrderRequest request
    ) {
        OrderCriteria.Order criteria = request.toCriteria();
        return ApiResponse.success(orderFacadeService.createOrder(criteria));
    }
}
