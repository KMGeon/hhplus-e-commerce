package kr.hhplus.be.server.application.payment;

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

import java.math.BigDecimal;

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
    public void 주문_조회_실패_시_복구_처리() {
        // given
        long userId = 1L;
        long orderId = 100L;
        Long userCouponId = 10L;
        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, userCouponId);

        when(orderService.isAvailableOrder(orderId)).thenThrow(new RuntimeException("주문을 찾을 수 없습니다"));

        // when
        paymentFacadeService.payment(criteria);

        // then
        verify(orderService).restoreOrderStatusCancel(orderId);
        verify(stockService).restoreStock(orderId);
        verify(paymentService).paymentProcessByBoolean(orderId, userId, BigDecimal.ZERO, false);

        verify(userCouponService, never()).checkUserCoupon(anyLong(), anyLong());
        verify(couponService, never()).calculateDiscountAmount(anyLong(), any());
        verify(userCouponService, never()).useCoupon(anyLong(), anyLong());
        verify(userService, never()).usePoint(anyLong(), any());
    }

    @Test
    public void 쿠폰_검증_실패_시_복구_처리() {
        // given
        long userId = 1L;
        long orderId = 100L;
        Long userCouponId = 10L;
        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, userCouponId);

        OrderInfo.OrderPaymentInfo orderInfo = new OrderInfo.OrderPaymentInfo(orderId, BigDecimal.valueOf(10_000));
        when(orderService.isAvailableOrder(orderId)).thenReturn(orderInfo);
        when(userCouponService.checkUserCoupon(userCouponId, orderId)).thenThrow(new RuntimeException("유효하지 않은 쿠폰입니다"));

        // when
        paymentFacadeService.payment(criteria);

        // then
        verify(orderService).isAvailableOrder(orderId);
        verify(userCouponService).checkUserCoupon(userCouponId, orderId);

        verify(orderService).restoreOrderStatusCancel(orderId);
        verify(stockService).restoreStock(orderId);
        verify(paymentService).paymentProcessByBoolean(orderId, userId, BigDecimal.ZERO, false);

        verify(couponService, never()).calculateDiscountAmount(anyLong(), any());
        verify(userCouponService, never()).useCoupon(anyLong(), anyLong());
        verify(orderService, never()).applyToDisCount(anyLong(), any());
        verify(userService, never()).usePoint(anyLong(), any());
    }

    @Test
    public void 포인트_사용_실패_시_복구_처리() {
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

        doThrow(new RuntimeException("포인트가 부족합니다")).when(userService).usePoint(userId, finalAmount);

        // when
        paymentFacadeService.payment(criteria);

        // then
        verify(orderService).isAvailableOrder(orderId);
        verify(userCouponService).checkUserCoupon(userCouponId, orderId);
        verify(couponService).calculateDiscountAmount(couponId, orderInfo.totalPrice());
        verify(userCouponService).useCoupon(userCouponId, orderId);
        verify(orderService).applyToDisCount(orderId, discountAmount);
        verify(userService).usePoint(userId, finalAmount);

        verify(orderService).restoreOrderStatusCancel(orderId);
        verify(stockService).restoreStock(orderId);
        verify(paymentService).paymentProcessByBoolean(orderId, userId, BigDecimal.ZERO, false);
    }
}