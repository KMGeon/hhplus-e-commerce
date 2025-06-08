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
import java.util.List;
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


    /**
     * todo : 2025 - 06 -08
     * 1. 결제와 재고를 MSA에서 도메인을 어떻게 분리를 처리할까?
     * 2. 각 도메인 마다 Application Event, Kafka 이벤트 분리하기
     * 3. Kafka 이벤트를 zero-payload / full-payload 결정하기 ( 학습 포인트 )
     * 4. 이벤트 분리를 시키고 각 상황에 맞는 보상 트랜잭션 (Saga Pattern) 을 어떻게 처리할지 고민하기
     * 5. 결제의 경우에는 아마도 트래픽이 적을테니 정합성을 check 100% 보장하게
     * 6. 서킷 브레이커 ?? 이놈이 먼지 한번 알아보자
     */
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
            orderService.addRankingSystemProducts(orderId);
            userService.usePoint(userId, finalTotalPrice);
            paymentService.paymentProcessByBoolean(orderId, userId, finalTotalPrice, true);
            log.info("결제 성공: orderId={}, userId={}, amount={}", orderId, userId, finalTotalPrice);
        } catch (Exception e) {
            log.error("결제 실패: orderId={}, userId={}, 원인={}", orderId, userId, e.getMessage(), e);
            orderService.restoreOrderStatusCancel(orderId);
            stockService.restoreStock(List.of(orderId));
            paymentService.paymentProcessByBoolean(orderId, userId, BigDecimal.ZERO, false);
            throw e;
        }
    }
}