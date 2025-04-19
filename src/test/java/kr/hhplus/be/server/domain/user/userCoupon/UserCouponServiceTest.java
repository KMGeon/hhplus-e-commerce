package kr.hhplus.be.server.domain.user.userCoupon;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

    @InjectMocks
    private UserCouponService userCouponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private CouponRepository couponRepository;

    @Test
    public void 쿠폰_발행_성공() {
        // given
        long userId = 1L;
        long couponId = 100L;
        UserCouponEntity userCouponEntity = UserCouponEntity.publishCoupon(userId, couponId);

        given(userCouponRepository.save(any(UserCouponEntity.class))).willReturn(userCouponEntity);

        // when
        userCouponService.save(userId, couponId);

        // then
        verify(userCouponRepository, times(1)).save(any(UserCouponEntity.class));
    }

    @Test
    public void 쿠폰_유효성_검증() {
        // given
        long userCouponId = 1L;
        long userId = 1L;
        long couponId = 100L;

        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .id(userCouponId)
                .userId(userId)
                .couponId(couponId)
                .couponStatus(CouponStatus.AVAILABLE)
                .build();

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        // when
        // then
        assertDoesNotThrow(() -> userCouponService.validateCoupon(userCouponId, userId));
        verify(userCouponRepository, times(1)).findById(userCouponId);
    }

    @Test
    public void 존재하지_않는_쿠폰_유효성() {
        // given
        long userCouponId = 1L;
        long userId = 1L;

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.empty());

        // when
        // then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userCouponService.validateCoupon(userCouponId, userId));
        assertEquals("사용자 쿠폰을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void 다른_사람이_발급한_쿠폰_사용_유효성() {
        // given
        long userCouponId = 1L;
        long userId = 1L;
        long differentUserId = 2L;
        long couponId = 100L;

        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .id(userCouponId)
                .userId(userId)
                .couponId(couponId)
                .couponStatus(CouponStatus.AVAILABLE)
                .build();

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> userCouponService.validateCoupon(userCouponId, differentUserId));
    }

    @Test
    public void 이미_사용한_쿠폰_실패() {
        // given
        long userCouponId = 1L;
        long userId = 1L;
        long couponId = 100L;
        long orderId = 200L;

        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .id(userCouponId)
                .userId(userId)
                .couponId(couponId)
                .couponStatus(CouponStatus.USED)
                .orderId(orderId)
                .build();

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> userCouponService.validateCoupon(userCouponId, userId));
    }

    @Test
    @DisplayName("할인액 계산 성공 테스트 - 정액 할인")
    public void 정액_할인_계산() {
        // given
        long userCouponId = 1L;
        long userId = 1L;
        long couponId = 100L;
        long orderId = 200L;
        BigDecimal orderAmount = new BigDecimal("10000");
        BigDecimal discountAmount = new BigDecimal("1000");

        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .id(userCouponId)
                .userId(userId)
                .couponId(couponId)
                .couponStatus(CouponStatus.AVAILABLE)
                .build();

        CouponEntity coupon = mock(CouponEntity.class);

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(coupon.calculateDiscountAmount(orderAmount)).willReturn(discountAmount);

        // when
        BigDecimal result = userCouponService.validateAndCalculateDiscount(userCouponId, userId, orderId, orderAmount);

        // then
        assertEquals(discountAmount, result);
        verify(userCouponRepository, times(1)).findById(userCouponId);
        verify(couponRepository, times(1)).findById(couponId);
        verify(coupon, times(1)).calculateDiscountAmount(orderAmount);
    }

    @Test
    public void 정액_할인_계산_쿠폰이_없어서_실패() {
        // given
        long userCouponId = 1L;
        long userId = 1L;
        long couponId = 100L;
        long orderId = 200L;
        BigDecimal orderAmount = new BigDecimal("10000");

        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .id(userCouponId)
                .userId(userId)
                .couponId(couponId)
                .couponStatus(CouponStatus.AVAILABLE)
                .build();

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));
        given(couponRepository.findById(couponId)).willReturn(Optional.empty());

        // when
        // then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userCouponService.validateAndCalculateDiscount(userCouponId, userId, orderId, orderAmount));
        assertEquals("쿠폰을 찾을 수 없습니다.", exception.getMessage());
    }
}