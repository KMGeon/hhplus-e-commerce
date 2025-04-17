package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentFacadeService {

    private final UserService userService;
    private final UserCouponService userCouponService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    @Transactional
    public void payment(PaymentCriteria.Pay criteria) {
        OrderInfo.OrderPaymentInfo orderInfo = orderService.isAvailableOrder(criteria.orderId());

        Long userId = userService.getUserId(criteria.userId());

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (criteria.userCouponId() != null) {
            discountAmount = userCouponService.validateAndCalculateDiscount(
                    criteria.userCouponId(),
                    userId,
                    orderInfo.orderId(),
                    orderInfo.totalPrice()
            );
        }

        orderService.setDiscountAmount(
                orderInfo.orderId(),
                discountAmount
        );

        userService.payProcess(userId, orderInfo.orderId());

        BigDecimal finalAmount = orderInfo.totalPrice().subtract(discountAmount);

        paymentService.processPayment(
                criteria.orderId(),
                userId,
                finalAmount
        );
    }
}