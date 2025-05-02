package kr.hhplus.be.server.application.payment;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

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
        Optional<Long> userCouponId = Optional.ofNullable(criteria.userCouponId());

        try {
            OrderInfo.OrderPaymentInfo orderInfo = orderService.isAvailableOrder(orderId);

            BigDecimal discountAmount = userCouponId
                    .map(couponId -> {
                        Long couponIdValue = userCouponService.checkUserCoupon(couponId, userId);
                        BigDecimal discount = couponService.calculateDiscountAmount(couponIdValue, orderInfo.totalPrice());
                        userCouponService.useCoupon(couponId, orderId);
                        return discount;
                    })
                    .orElse(BigDecimal.ZERO);

            BigDecimal finalTotalPrice = orderService.applyToDisCount(orderId, discountAmount);
            userService.usePoint(userId, finalTotalPrice);
            paymentService.paymentProcessByBoolean(orderId, userId, finalTotalPrice, true);

            log.info("결제 성공: orderId={}, userId={}, amount={}", orderId, userId, finalTotalPrice);
        } catch (Exception e) {
            log.error("결제 실패: orderId={}, userId={}, 원인={}", orderId, userId, e.getMessage(), e);
            orderService.restoreOrderStatusCancel(orderId);
            stockService.restoreStock(orderId);
            paymentService.paymentProcessByBoolean(orderId, userId, BigDecimal.ZERO, false);
            throw e;
        }
    }
}