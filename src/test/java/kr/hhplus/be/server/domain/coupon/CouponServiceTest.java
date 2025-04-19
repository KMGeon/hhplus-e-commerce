package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Test
    void 쿠폰_생성_성공() {
        // given
        String couponName = "신규 가입 쿠폰";
        CouponDiscountType discountType = CouponDiscountType.FIXED_AMOUNT;
        long initQuantity = 100L;
        BigDecimal discountAmount = BigDecimal.valueOf(5000);

        CouponCommand.Create command = new CouponCommand.Create(
                couponName,
                "FIXED_AMOUNT",
                initQuantity,
                1000
        );

        CouponEntity savedCoupon = mock(CouponEntity.class);
        when(savedCoupon.getId()).thenReturn(1L);
        when(couponRepository.save(any(CouponEntity.class))).thenReturn(savedCoupon);

        // when
        CouponInfo.CreateInfo result = couponService.save(command);

        // then
        assertEquals(1L, result.couponId());

        ArgumentCaptor<CouponEntity> couponCaptor = ArgumentCaptor.forClass(CouponEntity.class);
        verify(couponRepository).save(couponCaptor.capture());

        CouponEntity capturedCoupon = couponCaptor.getValue();
        assertNotNull(capturedCoupon);
    }

    @Test
    void 쿠폰_검증_및_수량감소_성공() {
        // given
        long couponId = 1L;
        CouponEntity coupon = mock(CouponEntity.class);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        couponService.validateAndDecreaseCoupon(couponId);

        // then
        verify(couponRepository).findById(couponId);
        verify(coupon).validateForPublish();
        verify(coupon).decreaseQuantity();
    }

    @Test
    void 쿠폰_검증_실패_쿠폰없음() {
        // given
        long nonExistentCouponId = 999L;
        when(couponRepository.findById(nonExistentCouponId)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(RuntimeException.class, () ->
                couponService.validateAndDecreaseCoupon(nonExistentCouponId));

        // then
        assertEquals("쿠폰이 존재하지 않습니다.", exception.getMessage());
        verify(couponRepository).findById(nonExistentCouponId);
    }

    @Test
    void 쿠폰_검증_실패_유효성검증실패() {
        // given
        long couponId = 1L;
        CouponEntity coupon = mock(CouponEntity.class);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        doThrow(new RuntimeException("쿠폰이 유효하지 않습니다.")).when(coupon).validateForPublish();

        // when
        Exception exception = assertThrows(RuntimeException.class, () ->
                couponService.validateAndDecreaseCoupon(couponId));

        // then
        assertEquals("쿠폰이 유효하지 않습니다.", exception.getMessage());
        verify(couponRepository).findById(couponId);
        verify(coupon).validateForPublish();
        verify(coupon, never()).decreaseQuantity();
    }

    @Test
    void 쿠폰_수량감소_실패() {
        // given
        long couponId = 1L;
        CouponEntity coupon = mock(CouponEntity.class);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        doNothing().when(coupon).validateForPublish();
        doThrow(new RuntimeException("남은 쿠폰이 없습니다.")).when(coupon).decreaseQuantity();

        // when
        Exception exception = assertThrows(RuntimeException.class, () ->
                couponService.validateAndDecreaseCoupon(couponId));

        // then
        assertEquals("남은 쿠폰이 없습니다.", exception.getMessage());
        verify(couponRepository).findById(couponId);
        verify(coupon).validateForPublish();
        verify(coupon).decreaseQuantity();
    }
}