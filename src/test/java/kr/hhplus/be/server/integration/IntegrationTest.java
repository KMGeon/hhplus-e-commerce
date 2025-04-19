package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.application.payment.PaymentFacadeService;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.CouponStatus;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserCouponJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
public class IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    OrderFacadeService orderFacadeService;

    @Autowired
    private CouponFacadeService couponFacadeService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentFacadeService paymentFacadeService;


    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserCouponJpaRepository couponJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    @DisplayName("""
             1. 유저가 포인트를 충전
             2. 쿠폰을 발급하고
             3. 주문을 생성한다. 
             4. 이후 주문을 결제한다. (쿠폰을 사용한다.)
            """)
    @Transactional
    public void 시나리오_1() throws Exception {
        // 유저가 포인트를 충전한다.
        userService.charge(new UserCommand.PointCharge(1L, 10000L));

        // 쿠폰을 만든다.
        couponService.save(new CouponCommand.Create("생일 축하해요 쿠폰", "FIXED_AMOUNT", 10L, 1000L));
        // 유저가 쿠폰을 발급한다
        couponFacadeService.publishCoupon(new CouponCriteria.PublishCriteria(1L, 1L));


        // 총 10_550원
        List<OrderCriteria.Item> items = List.of(
                new OrderCriteria.Item("DL-XPS-15", 1L), // 2200원
                new OrderCriteria.Item("DL-AW-M16", 2L), // 6400원
                new OrderCriteria.Item("SN-PS5", 3L) // 1950원
        );


        // 유저가 주문을 생성한다.
        orderFacadeService.createOrder(new OrderCriteria.Order(1L, items));

        // 유저가 주문을 결제한다.
        paymentFacadeService.payment(new PaymentCriteria.Pay(1L, 1L, 1L));

        UserEntity userEntity = userJpaRepository.findById(1L)
                .orElseThrow();
        UserCouponEntity userCouponEntity = couponJpaRepository.findById(1L)
                .orElseThrow();
        OrderEntity orderEntity = orderJpaRepository.findById(1L)
                .orElseThrow();

        assertEquals(userEntity.getPoint(), 450L, "유저 10_000원 충전 후 주문 총 가격 10_550원 + 쿠폰 1_000원 고정가 할인하여 450원 남음");
        assertEquals(userCouponEntity.getCouponStatus(), CouponStatus.USED, "1번 쿠폰을 사용하고 사용 완료 상태");
        assertEquals(orderEntity.getStatus(), OrderStatus.PAID, "결제가 완료되어서 PAID 상태");
        assertEquals(orderEntity.getTotalPrice(), BigDecimal.valueOf(10550), "2200 + 6400*2 + 1950 - 1000 = 10550");
        assertEquals(orderEntity.getDiscountAmount(), BigDecimal.valueOf(1000.0), "쿠폰 고정가 1000원 할인");
    }
    
    
    @Test
    public void 시나리오_2() throws Exception{
        // given
        
        // when
    
        // then
    }
}
