package kr.hhplus.be.server.config;

import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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





    protected static final Long EXIST_USER = 1L;
    protected static final Long NEW_USER = 100L;
}
