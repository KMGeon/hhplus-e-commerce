package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.event.CouponEvent;
import kr.hhplus.be.server.domain.coupon.event.CouponEventPublisher;
import kr.hhplus.be.server.domain.support.OutboxEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private OutboxEventPublisher outboxEventPublisher;

    @Mock
    private CouponEventPublisher couponEventPublisher;

    @InjectMocks
    private CouponService couponService;

    @Test
    void 쿠폰_생성_성공() {
        // given
        CouponCommand.Create command = new CouponCommand.Create(
                "테스트 쿠폰", "FIXED_AMOUNT", 100, 5000
        );

        CouponEntity savedCoupon = CouponEntity.builder()
                .id(1L)
                .name(command.couponName())
                .discountType(CouponDiscountType.FIXED_AMOUNT)
                .initQuantity(command.initQuantity())
                .remainQuantity(command.initQuantity())
                .discountAmount(command.discountAmount())
                .expireTime(LocalDateTime.now().plusDays(10))
                .build();

        when(couponRepository.save(any(CouponEntity.class))).thenReturn(savedCoupon);

        // when
        CouponInfo.CreateInfo result = couponService.save(command);

        // then
        assertThat(result.couponId()).isEqualTo(1L);
        verify(couponRepository, times(1)).save(any(CouponEntity.class));
    }

    @Test
    void 쿠폰_수량_감소_정상_처리() {
        // given
        long couponId = 1L;
        CouponEntity coupon = spy(CouponEntity.createCoupon(
                "테스트 쿠폰", "FIXED_AMOUNT", 100, 5000, LocalDateTime.now()));

        when(couponRepository.issueCoupon(any(), any())).thenReturn(couponId);
        doNothing().when(outboxEventPublisher).publish(any(), any());
        doNothing().when(couponEventPublisher).publishCouponToDecrease(any());
//        couponEventPublisher
        // when
        couponService.publishCoupon(
                new CouponCommand.Publish(1L, couponId));

        // then
        verify(outboxEventPublisher, times(1)).publish(any(), any());
        verify(couponRepository, times(1)).issueCoupon(any(), any());
    }

    @Test
    void 쿠폰_수량_부족_시_예외_발생() {
        // given
        long couponId = 1L;
        CouponEntity coupon = spy(CouponEntity.builder()
                .name("테스트 쿠폰")
                .discountType(CouponDiscountType.FIXED_AMOUNT)
                .initQuantity(100)
                .remainQuantity(0) // 남은 수량 0
                .discountAmount(5000)
                .expireTime(LocalDateTime.now().plusDays(10))
                .build());

        doThrow(new RuntimeException("쿠폰이 모두 소진되었습니다.")).when(couponRepository).issueCoupon(any(),any());

        // when
        // then
        assertThatThrownBy(() -> couponService.publishCoupon(new CouponCommand.Publish(1L, couponId)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("쿠폰이 모두 소진되었습니다");

        verify(couponRepository, times(1)).issueCoupon(any(), any());
    }

    @Test
    void 쿠폰_중복_발급_오류() {
        // given
        long couponId = 1L;
        CouponEntity coupon = spy(CouponEntity.builder()
                .name("테스트 쿠폰")
                .discountType(CouponDiscountType.FIXED_AMOUNT)
                .initQuantity(100)
                .remainQuantity(0) // 남은 수량 0
                .discountAmount(5000)
                .expireTime(LocalDateTime.now().plusDays(10))
                .build());

        doThrow(new RuntimeException("이미 발급한 쿠폰입니다.")).when(couponRepository).issueCoupon(any(),any());

        // when
        // then
        assertThatThrownBy(() -> couponService.publishCoupon(new CouponCommand.Publish(1L, couponId)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 발급한 쿠폰입니다.");

        verify(couponRepository, times(1)).issueCoupon(any(), any());
    }

    @Test
    void 정액_할인_금액_계산() {
        // given
        long couponId = 1L;
        BigDecimal totalPrice = BigDecimal.valueOf(10000);
        double discountAmount = 3000;

        CouponEntity coupon = CouponEntity.createCoupon(
                "정액 할인 쿠폰", "FIXED_AMOUNT", 100, discountAmount, LocalDateTime.now());

        when(couponRepository.findCouponById(couponId)).thenReturn(coupon);

        // when
        BigDecimal result = couponService.calculateDiscountAmount(couponId, totalPrice);

        // then
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(discountAmount));
        verify(couponRepository, times(1)).findCouponById(couponId);
    }

    @Test
    void 퍼센트_할인_금액_계산() {
        // given
        long couponId = 1L;
        BigDecimal totalPrice = BigDecimal.valueOf(10000);
        double discountPercentage = 20; // 20%

        CouponEntity coupon = CouponEntity.createCoupon(
                "퍼센트 할인 쿠폰", "PERCENTAGE", 100, discountPercentage, LocalDateTime.now());

        when(couponRepository.findCouponById(couponId)).thenReturn(coupon);

        // when
        BigDecimal result = couponService.calculateDiscountAmount(couponId, totalPrice);

        // then
        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(2000)); // 10000의 20%
        verify(couponRepository, times(1)).findCouponById(couponId);
    }

    @Test
    void 정액_할인_주문금액보다_큰_경우_주문금액만큼_할인() {
        // given
        long couponId = 1L;
        BigDecimal totalPrice = BigDecimal.valueOf(5000);
        double discountAmount = 10000; // 주문금액보다 큰 할인액

        CouponEntity coupon = CouponEntity.createCoupon(
                "정액 할인 쿠폰", "FIXED_AMOUNT", 100, discountAmount, LocalDateTime.now());

        when(couponRepository.findCouponById(couponId)).thenReturn(coupon);

        // when
        BigDecimal result = couponService.calculateDiscountAmount(couponId, totalPrice);

        // then
        assertThat(result).isEqualByComparingTo(totalPrice); // 주문금액까지만 할인
        verify(couponRepository, times(1)).findCouponById(couponId);
    }
}