package kr.hhplus.be.server.domain.user.userCoupon;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public void save(long userId, long couponId) {
        userCouponRepository.save(UserCouponEntity.publishCoupon(userId, couponId));
    }

    @Transactional
    public void validateCoupon(Long userCouponId, Long userId) {
        UserCouponEntity userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new RuntimeException("사용자 쿠폰을 찾을 수 없습니다."));
        userCoupon.isEqualUser(userId);
        userCoupon.validateAvailable();
    }

    @Transactional(readOnly = true)
    public BigDecimal validateAndCalculateDiscount(Long userCouponId, Long userId, Long orderId, BigDecimal orderAmount) {
        UserCouponEntity userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new RuntimeException("사용자 쿠폰을 찾을 수 없습니다."));

        userCoupon.isEqualUser(userId);

        userCoupon.validateAvailable();

        CouponEntity coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다."));

        BigDecimal rtn = coupon.calculateDiscountAmount(orderAmount);

        userCoupon.use(orderId);
        return rtn;
    }
}
