package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;


    @Override
    public CouponEntity save(CouponEntity coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Optional<CouponEntity> findById(Long id) {
        return couponJpaRepository.findById(id);
    }
}
