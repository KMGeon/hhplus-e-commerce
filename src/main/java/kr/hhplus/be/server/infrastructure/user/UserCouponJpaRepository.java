package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCouponJpaRepository extends JpaRepository<UserCouponEntity, Long> {
    @Query(value = "SELECT uc from UserCouponEntity uc where uc.userId = :userId and uc.couponId = :couponId")
    UserCouponEntity findByUserIdAndCouponId(@Param("userId") long userId, @Param("couponId") long couponId);
}
