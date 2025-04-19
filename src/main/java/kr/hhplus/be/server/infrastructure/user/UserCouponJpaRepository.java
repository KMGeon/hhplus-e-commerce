package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponJpaRepository extends JpaRepository<UserCouponEntity, Long> {
    boolean existsByUserIdAndCouponId(long userId, long couponId);
}
