package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.userCoupon.CouponStatus;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class IntegrationTest extends ApplicationContext {

    @Test
    @DisplayName("""
             1. 유저가 포인트를 충전
             2. 쿠폰을 발급하고
             3. 주문을 생성한다.
             4. 이후 주문을 결제한다. (쿠폰을 사용한다.)
            """)
    public void 시나리오_1() throws Exception {
        // redis 초기화
        redisTemplateRepository.flushAll();
        // 유저가 포인트를 충전한다.
        userService.charge(new UserCommand.PointCharge(EXIST_USER, 1000L));
        // 쿠폰을 만든다.
        CouponInfo.CreateInfo getCoupon = couponService.save(new CouponCommand.Create("생일 축하해요 쿠폰", "FIXED_AMOUNT", 10L, 1000L));
        // 유저가 쿠폰을 발급한다
        couponService.publishCoupon(new CouponCriteria.PublishCriteria(EXIST_USER, getCoupon.couponId()));

        // coupon 스케줄러 > 10000
        Thread.sleep(10005L);

        // 총 주문 금액 계산
        List<OrderCriteria.Item> items = List.of(
                new OrderCriteria.Item("A-0001-0001", 1L),  // 2000
                new OrderCriteria.Item("A-0001-0002", 2L),  // 2100 * 2 = 4200
                new OrderCriteria.Item("A-0001-0003", 3L)   // 2200 * 3 = 6600
                // 합계: 2000 + 4200 + 6600 = 12800
        );


        // 유저가 주문을 생성한다.
        Long orderId = orderFacadeService.createOrder(new OrderCriteria.Order(EXIST_USER, items));

        // 유저가 주문을 결제한다.
        paymentFacadeService.payment(new PaymentCriteria.Pay(EXIST_USER, orderId, 1L));

        UserEntity userEntity = userJpaRepository.findById(EXIST_USER)
                .orElseThrow();
        UserCouponEntity userCouponEntity = userCouponJpaRepository.findById(1L)
                .orElseThrow();
        OrderEntity orderEntity = orderJpaRepository.findById(orderId)
                .orElseThrow();

        // 결제 후 검증
        assertEquals(89200, userEntity.getPoint(), "기존 100_000원 + 1_000원 포인트 충전 - 12_800원 상품가격 + 1_000원 쿠폰 사용");
        assertEquals(CouponStatus.USED, userCouponEntity.getCouponStatus(), "쿠폰 상태는 USED여야 함");
        assertEquals(OrderStatus.PAID, orderEntity.getStatus(), "주문 상태는 PAID여야 함");
        assertEquals(new BigDecimal("12800.00"), orderEntity.getTotalPrice());
        assertEquals(new BigDecimal("1000.00"), orderEntity.getDiscountAmount());
    }

    @Test
    @DisplayName("""
             1. 유저가 포인트를 충전
             2. 주문을 생성한다.
             3. 이후 주문을 결제한다. (쿠폰 X)
            """)
    public void 시나리오_2() throws Exception {
        // 유저가 포인트를 충전한다.
        userService.charge(new UserCommand.PointCharge(EXIST_USER, 1000L));

        List<OrderCriteria.Item> items = List.of(
                new OrderCriteria.Item("A-0001-0001", 1L),  // 2000
                new OrderCriteria.Item("A-0001-0002", 2L),  // 2100 * 2 = 4200
                new OrderCriteria.Item("A-0001-0003", 3L)   // 2200 * 3 = 6600
                // 합계: 2000 + 4200 + 6600 = 12800
        );


        // 유저가 주문을 생성한다.
        Long orderId = orderFacadeService.createOrder(new OrderCriteria.Order(EXIST_USER, items));

        // 유저가 주문을 결제한다.
        paymentFacadeService.payment(new PaymentCriteria.Pay(EXIST_USER, orderId, null));

        UserEntity userEntity = userJpaRepository.findById(EXIST_USER)
                .orElseThrow();
        OrderEntity orderEntity = orderJpaRepository.findById(orderId)
                .orElseThrow();

        // 결제 후 검증
        assertEquals(88200, userEntity.getPoint(), "기존 100_000원 + 1_000원 포인트 충전 - 12_800원 상품가격 + 1_000원 쿠폰 사용");
        assertEquals(OrderStatus.PAID, orderEntity.getStatus(), "주문 상태는 PAID여야 함");
        assertEquals(new BigDecimal("12800.00"), orderEntity.getTotalPrice());
        assertEquals(new BigDecimal("0.00"), orderEntity.getDiscountAmount());
    }

    @Test
    @DisplayName("""
            1. 유저가 포인트를 충전한다.
            2. 주문을 생성한다.
            3. 결제를 처리한다.
                - 결제 과정에서 존재하지 않는 쿠폰을 기입한다.
                - 실패한다.
            """)
    public void 시나리오_3() throws Exception{
        userService.charge(new UserCommand.PointCharge(EXIST_USER, 1000L));
        Long orderId = createOrder(EXIST_USER);
        Assertions.assertThatThrownBy(() -> paymentFacadeService.payment(new PaymentCriteria.Pay(EXIST_USER, orderId, 1L)))
                        .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("사용자 쿠폰을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("""
            1. 유저가 포인트가 없다.
            2. 주문을 생성한다.
            3. 주문을 결제한다.
                3-1. 결제가 처리되면 안된다.
            """)
    public void 시나리오_4() throws Exception{
        UserEntity getUser = userJpaRepository.save(UserEntity.createNewUser());
        Long orderId = createOrder(getUser.getId());
        Assertions.assertThatThrownBy(() -> paymentFacadeService.payment(new PaymentCriteria.Pay(getUser.getId(), orderId, null)))
                        .isInstanceOf(IllegalStateException.class);
    }

    private Long createOrder(Long userId) {
        List<OrderCriteria.Item> items = List.of(
                new OrderCriteria.Item("A-0001-0001", 1L),  // 2000
                new OrderCriteria.Item("A-0001-0002", 2L),  // 2100 * 2 = 4200
                new OrderCriteria.Item("A-0001-0003", 3L)   // 2200 * 3 = 6600
                // 합계: 2000 + 4200 + 6600 = 12800
        );
        return orderFacadeService.createOrder(new OrderCriteria.Order(userId, items));
    }
}
