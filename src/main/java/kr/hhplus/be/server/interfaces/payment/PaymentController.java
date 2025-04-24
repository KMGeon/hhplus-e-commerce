package kr.hhplus.be.server.interfaces.payment;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.payment.PaymentFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerDocs {

    private final PaymentFacadeService paymentFacadeService;

    @PostMapping
    public void processPayment(@Valid @RequestBody PaymentRequest.PayRequest paymentRequest) {
        paymentFacadeService.payment(paymentRequest.toCriteria());
    }
}
