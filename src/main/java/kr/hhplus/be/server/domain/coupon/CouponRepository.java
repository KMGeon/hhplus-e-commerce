package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;

public interface CouponRepository {
     CouponEntity save(CouponEntity coupon);
     Optional<CouponEntity> findById(Long id);
}
