package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.PaymentCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacadeService {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;


    public String payment(PaymentCommand command) {
        // 유저 체크하기
        userService.isValidUser(command.userId());

        // 주문 id, exprire 검증
        orderService.isValidOrder(command.orderId());

        //주문 상태 변경하고 -> 결제중

        // 유저 포인트 조회해서

        return null;
    }
}
