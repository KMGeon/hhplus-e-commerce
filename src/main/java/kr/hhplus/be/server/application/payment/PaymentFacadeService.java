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

    private static final int MAX_RETRY = 10;

    @Transactional
    public void payment(PaymentCriteria.Pay criteria) {
        long orderId = criteria.orderId();
        long userId = criteria.userId();
        Long userCouponId = criteria.userCouponId();

        try {
            OrderInfo.OrderPaymentInfo orderInfo = orderService.isAvailableOrder(orderId);

            BigDecimal discountAmount = BigDecimal.ZERO;
            if (userCouponId != null) {
                Long couponId = userCouponService.checkUserCoupon(userCouponId, orderId);
                discountAmount = couponService.calculateDiscountAmount(couponId, orderInfo.totalPrice());
                userCouponService.useCoupon(userCouponId, orderId);
            }

            BigDecimal finalTotalPrice = orderService.applyToDisCount(orderId, discountAmount);

            for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
                try {
                    // 낙관적 락 적용
                    userService.usePoint(userId, finalTotalPrice);
                    break;
                } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                    if (attempt == MAX_RETRY - 1) {
                        log.error("결제 실패 - 낙관적 락 재시도 최대 횟수({})를 초과: orderId={}, userId={}",
                                MAX_RETRY, orderId, userId);
                        throw e;
                    }

                    log.warn("결제 재시도 {}회 실패 (낙관적 락 충돌): orderId={}, userId={}",
                            attempt + 1, orderId, userId);

                    long backoffMs = (long) Math.pow(2, attempt) * 100L;
                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("스레드 인터럽트 발생", ie);
                    }
                }
            }

            paymentService.paymentProcessByBoolean(orderId, userId, finalTotalPrice, true);
            log.info("결제 성공: orderId={}, userId={}, amount={}", orderId, userId, finalTotalPrice);
        } catch (Exception e) {
            log.error("결제 실패: orderId={}, userId={}, 원인={}", orderId, userId, e.getMessage());
            orderService.restoreOrderStatusCancel(orderId);
            stockService.restoreStock(orderId);
            paymentService.paymentProcessByBoolean(orderId, userId, BigDecimal.ZERO, false);
            throw e;
        }
    }
}