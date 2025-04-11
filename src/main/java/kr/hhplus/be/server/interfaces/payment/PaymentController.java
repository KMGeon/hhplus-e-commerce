package kr.hhplus.be.server.interfaces.payment;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.payment.PaymentFacadeService;
import kr.hhplus.be.server.interfaces.common.ApiResponse;
import kr.hhplus.be.server.interfaces.order.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacadeService paymentFacadeService;

    @PostMapping("/payment")
    public ApiResponse<Integer> payment(
            @Valid @RequestBody PaymentRequestDTO.PayRequest request
    ) {

        paymentFacadeService.payment(request.toCommand());
        return ApiResponse.maskToInteger(1);
    }

}
