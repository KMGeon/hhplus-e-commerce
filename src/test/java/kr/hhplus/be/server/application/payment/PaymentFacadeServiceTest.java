package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentFacadeService paymentFacadeService;

    @Test
    void 결제_파사드_순서_호출() {
        // given
        long userId = 1L;
        long orderId = 1L;
        long userCouponId = 1L;
        BigDecimal totalPrice = new BigDecimal("10000");
        BigDecimal discountAmount = new BigDecimal("2000");

        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(userId, orderId, userCouponId);
        OrderInfo.OrderPaymentInfo orderInfo = new OrderInfo.OrderPaymentInfo(orderId, totalPrice);

        when(orderService.isAvailableOrder(anyLong())).thenReturn(orderInfo);
        when(userService.getUserId(anyLong())).thenReturn(userId);
        when(userCouponService.validateAndCalculateDiscount(anyLong(), anyLong(), anyLong(), any()))
                .thenReturn(discountAmount);

        // when
        paymentFacadeService.payment(criteria);

        // then
        InOrder inOrder = inOrder(orderService, userService, userCouponService, paymentService);
        
        inOrder.verify(orderService).isAvailableOrder(orderId);
        inOrder.verify(userService).getUserId(userId);
        inOrder.verify(userCouponService).validateAndCalculateDiscount(userCouponId, userId, orderId, totalPrice);
        inOrder.verify(orderService).setDiscountAmount(orderId, discountAmount);
        inOrder.verify(userService).payProcess(userId, orderId);
        inOrder.verify(paymentService).processPayment(orderId, userId, totalPrice.subtract(discountAmount));
    }
} 