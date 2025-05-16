package kr.hhplus.be.server.domain.user.userCoupon;

import java.util.List;

public interface UserCouponRepository {
    UserCouponEntity save(UserCouponEntity userCouponEntity);
    UserCouponEntity findById(long userCouponId);
    void saveAll(List<UserCouponEntity> userCouponEntities);
}