package kr.hhplus.be.server.domain.user.userCoupon;

import java.util.Optional;

public interface UserCouponRepository {
    UserCouponEntity save(UserCouponEntity userCouponEntity);
    UserCouponEntity findById(long userCouponId);
    UserCouponEntity findByUserIdAndCouponId(long userId, long couponId);
}