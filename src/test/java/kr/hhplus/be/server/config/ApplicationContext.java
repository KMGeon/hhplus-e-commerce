package kr.hhplus.be.server.config;

import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import kr.hhplus.be.server.application.payment.PaymentFacadeService;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.stock.StockJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserCouponJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = {"/setup.sql", "/clean.sql"})
public abstract class ApplicationContext {

    @Autowired
    protected UserService userService;
    @Autowired
    protected CouponService couponService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserJpaRepository userJpaRepository;
    @Autowired
    protected CouponFacadeService couponFacadeService;
    @Autowired
    protected CouponRepository couponRepository;
    @Autowired
    protected CouponJpaRepository couponJpaRepository;
    @Autowired
    protected OrderFacadeService orderFacadeService;
    @Autowired
    protected StockJpaRepository stockRepository;
    @Autowired
    protected OrderJpaRepository orderJpaRepository;
    @Autowired
    protected PaymentFacadeService paymentFacadeService;
    @Autowired
    protected UserCouponJpaRepository userCouponJpaRepository;


    protected static final Long EXIST_USER = 1L;
    protected static final Long NEW_USER = 100L;
}
