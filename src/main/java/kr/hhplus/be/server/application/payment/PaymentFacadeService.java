package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFacadeService {

    private final UserCouponService userCouponService;
    private final OrderService orderService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final CouponService couponService;
    private final StockService stockService;

    @Transactional
    public void payment(PaymentCriteria.Pay criteria) {
        long orderId = criteria.orderId();
        long userId = criteria.userId();
        Long userCouponId = criteria.userCouponId();
        try {
            OrderInfo.OrderPaymentInfo orderInfo = orderService.isAvailableOrder(orderId);
            Long couponId = userCouponService.checkUserCoupon(userCouponId, orderId);

            BigDecimal discountAmount = BigDecimal.ZERO;

            if (userCouponId != null) {
                discountAmount = couponService.calculateDiscountAmount(couponId, orderInfo.totalPrice());
                userCouponService.useCoupon(userCouponId, orderId);
            }

            BigDecimal finalTotalPrice = orderService.applyToDisCount(orderId, discountAmount);
            userService.usePoint(userId, finalTotalPrice);

            paymentService.paymentProcessByBoolean(orderId, userId, finalTotalPrice, true);
        } catch (Exception e) {
            orderService.restoreOrderStatusCancel(orderId);
            stockService.restoreStock(orderId);
            paymentService.paymentProcessByBoolean(orderId, userId, BigDecimal.ZERO, false);
            log.info("payment error {}", e.getMessage());
        }
    }
}