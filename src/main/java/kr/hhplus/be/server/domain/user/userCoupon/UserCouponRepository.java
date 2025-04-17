package kr.hhplus.be.server.domain.user.userCoupon;

import java.util.Optional;

public interface UserCouponRepository {
    UserCouponEntity save(UserCouponEntity userCouponEntity);
    Optional<UserCouponEntity> findById(long userCouponId);
    Optional<UserCouponEntity> findByIdWithCoupon(long userCouponId);
    boolean existsCoupon(long userId, long couponId);
}