package kr.hhplus.be.server.application.payment;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeServiceTest {

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private CouponService couponService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private PaymentFacadeService paymentFacadeService;

    @Test
    public void 쿠폰_미적용_결제하기() {
        // given
        long userId = 1L;
        long orderId = 100L;
        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, null);

        when(orderService.isAvailableOrder(orderId))
                .thenReturn(new OrderInfo.OrderPaymentInfo(orderId, BigDecimal.valueOf(10_000)));
        when(orderService.applyToDisCount(orderId, BigDecimal.ZERO))
                .thenReturn(BigDecimal.valueOf(10_000));

        // when
        paymentFacadeService.payment(criteria);

        // then
        verify(orderService, times(1)).isAvailableOrder(orderId);
        verify(orderService, times(1)).applyToDisCount(orderId, BigDecimal.ZERO);
        verify(userService, times(1)).usePoint(userId, BigDecimal.valueOf(10_000));
        verify(paymentService, times(1)).paymentProcessByBoolean(orderId, userId, BigDecimal.valueOf(10_000), true);
        verify(userCouponService, never()).checkUserCoupon(anyLong(), anyLong());
        verify(couponService, never()).calculateDiscountAmount(anyLong(), any());
        verify(userCouponService, never()).useCoupon(anyLong(), anyLong());
    }

    @Test
    public void 쿠폰_사용하고_결제하기() {
        // given
        long userId = 1L;
        long orderId = 100L;
        Long userCouponId = 10L;
        Long couponId = 1000L;
        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, userCouponId);

        OrderInfo.OrderPaymentInfo orderInfo = new OrderInfo.OrderPaymentInfo(orderId, BigDecimal.valueOf(10_000));
        when(orderService.isAvailableOrder(orderId)).thenReturn(orderInfo);
        when(userCouponService.checkUserCoupon(userCouponId, orderId)).thenReturn(couponId);

        BigDecimal discountAmount = BigDecimal.valueOf(2_000);
        when(couponService.calculateDiscountAmount(couponId, orderInfo.totalPrice())).thenReturn(discountAmount);

        BigDecimal finalAmount = BigDecimal.valueOf(8_000);
        when(orderService.applyToDisCount(orderId, discountAmount)).thenReturn(finalAmount);

        // when
         paymentFacadeService.payment(criteria);

        // then
        InOrder inOrder = inOrder(orderService, userCouponService, couponService, userService, paymentService);
        inOrder.verify(orderService).isAvailableOrder(orderId);
        inOrder.verify(userCouponService).checkUserCoupon(userCouponId, orderId);
        inOrder.verify(couponService).calculateDiscountAmount(couponId, orderInfo.totalPrice());
        inOrder.verify(userCouponService).useCoupon(userCouponId, orderId);
        inOrder.verify(orderService).applyToDisCount(orderId, discountAmount);
        inOrder.verify(userService).usePoint(userId, finalAmount);
        inOrder.verify(paymentService).paymentProcessByBoolean(orderId, userId, finalAmount, true);
    }
    @Test
    public void 결제_성공_테스트() {
        // given
        long userId = 1L;
        long orderId = 100L;
        Long userCouponId = 10L;
        Long couponId = 1000L;
        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, userCouponId);

        OrderInfo.OrderPaymentInfo orderInfo = new OrderInfo.OrderPaymentInfo(orderId, BigDecimal.valueOf(10_000));
        when(orderService.isAvailableOrder(orderId)).thenReturn(orderInfo);
        when(userCouponService.checkUserCoupon(userCouponId, orderId)).thenReturn(couponId);

        BigDecimal discountAmount = BigDecimal.valueOf(2_000);
        when(couponService.calculateDiscountAmount(couponId, orderInfo.totalPrice())).thenReturn(discountAmount);

        BigDecimal finalAmount = BigDecimal.valueOf(8_000);
        when(orderService.applyToDisCount(orderId, discountAmount)).thenReturn(finalAmount);

        // when
        paymentFacadeService.payment(criteria);

        // then
        verify(orderService).isAvailableOrder(orderId);
        verify(userCouponService).checkUserCoupon(userCouponId, orderId);
        verify(couponService).calculateDiscountAmount(couponId, orderInfo.totalPrice());
        verify(userCouponService).useCoupon(userCouponId, orderId);
        verify(orderService).applyToDisCount(orderId, discountAmount);
        verify(userService).usePoint(userId, finalAmount);
        verify(paymentService).paymentProcessByBoolean(orderId, userId, finalAmount, true);

        // 복구 로직이 호출되지 않아야 함
        verify(orderService, never()).restoreOrderStatusCancel(anyLong());
        verify(stockService, never()).restoreStock(anyLong());
    }

    @Test
    public void 쿠폰없이_결제_성공_테스트() {
        // given
        long userId = 1L;
        long orderId = 100L;
        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, null); // 쿠폰 없음

        OrderInfo.OrderPaymentInfo orderInfo = new OrderInfo.OrderPaymentInfo(orderId, BigDecimal.valueOf(10_000));
        when(orderService.isAvailableOrder(orderId)).thenReturn(orderInfo);

        // 할인 없이 원래 가격 그대로 사용
        when(orderService.applyToDisCount(orderId, BigDecimal.ZERO)).thenReturn(BigDecimal.valueOf(10_000));

        // when
        paymentFacadeService.payment(criteria);

        // then
        verify(orderService).isAvailableOrder(orderId);
        verify(userCouponService, never()).checkUserCoupon(anyLong(), anyLong());
        verify(couponService, never()).calculateDiscountAmount(anyLong(), any());
        verify(userCouponService, never()).useCoupon(anyLong(), anyLong());
        verify(orderService).applyToDisCount(orderId, BigDecimal.ZERO);
        verify(userService).usePoint(userId, BigDecimal.valueOf(10_000));
        verify(paymentService).paymentProcessByBoolean(orderId, userId, BigDecimal.valueOf(10_000), true);
    }

    @Test
    public void 낙관적_락_충돌_후_재시도_성공_테스트() {
        // given
        long userId = 1L;
        long orderId = 100L;
        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, null);

        OrderInfo.OrderPaymentInfo orderInfo = new OrderInfo.OrderPaymentInfo(orderId, BigDecimal.valueOf(10_000));
        when(orderService.isAvailableOrder(orderId)).thenReturn(orderInfo);
        when(orderService.applyToDisCount(orderId, BigDecimal.ZERO)).thenReturn(BigDecimal.valueOf(10_000));

        AtomicInteger count = new AtomicInteger(0);
        doAnswer(invocation -> {
            if (count.getAndIncrement() == 0) {
                throw new ObjectOptimisticLockingFailureException("", new OptimisticLockException());
            }
            return null;
        }).when(userService).usePoint(userId, BigDecimal.valueOf(10_000));

        // when
        try {
            paymentFacadeService.payment(criteria);
            fail("예외가 발생해야 합니다");
        } catch (ObjectOptimisticLockingFailureException e) {
            paymentFacadeService.payment(criteria);
        }

        // then
        verify(userService, times(2)).usePoint(userId, BigDecimal.valueOf(10_000));
        verify(paymentService).paymentProcessByBoolean(orderId, userId, BigDecimal.valueOf(10_000), true);
    }

    @Test
    public void 결제중_예상치못한_예외_발생시_복구_처리() {
        // given
        long userId = 1L;
        long orderId = 100L;
        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, null);

        OrderInfo.OrderPaymentInfo orderInfo = new OrderInfo.OrderPaymentInfo(orderId, BigDecimal.valueOf(10_000));
        when(orderService.isAvailableOrder(orderId)).thenReturn(orderInfo);
        when(orderService.applyToDisCount(orderId, BigDecimal.ZERO)).thenReturn(BigDecimal.valueOf(10_000));

        // usePoint는 성공했지만 paymentProcess에서 예외 발생
        doThrow(new RuntimeException("결제 처리 중 오류 발생"))
                .when(paymentService).paymentProcessByBoolean(orderId, userId, BigDecimal.valueOf(10_000), true);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            paymentFacadeService.payment(criteria);
        });

        verify(orderService).isAvailableOrder(orderId);
        verify(orderService).applyToDisCount(orderId, BigDecimal.ZERO);
        verify(userService).usePoint(userId, BigDecimal.valueOf(10_000));

        // 복구 로직이 호출되었는지 확인
        verify(orderService).restoreOrderStatusCancel(orderId);
        verify(stockService).restoreStock(orderId);
        verify(paymentService).paymentProcessByBoolean(orderId, userId, BigDecimal.ZERO, false);
    }
}