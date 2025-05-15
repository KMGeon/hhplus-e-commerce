package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponJpaRepository extends JpaRepository<CouponEntity , Long> {
    @Query(value = "SELECT c FROM coupon c WHERE  c.expireTime > now()")
    List<CouponEntity> findCouponByNotExpired();

}
