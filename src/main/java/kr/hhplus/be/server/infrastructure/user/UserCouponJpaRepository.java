package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCouponEntity, Long> {
    boolean existsByUserIdAndCouponId(long userId, long couponId);

    @Query("SELECT uc FROM UserCouponEntity uc JOIN FETCH uc.coupon WHERE uc.id = :userCouponId")
    Optional<UserCouponEntity> findByIdWithCoupon(@Param("userCouponId") long userCouponId);
}
