package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponCacheRepository cacheRepository;


    @Override
    public CouponEntity save(CouponEntity coupon) {
        return couponJpaRepository.save(coupon);
    }



    @Override
    public CouponEntity findCouponById(Long id) {
        return couponJpaRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException(String.format("쿠폰이 존재하지 않습니다. id: %s", id)));
    }

    @Override
    public void initializeCoupon(Long couponId, Long quantity) {
         cacheRepository.initializeCoupon(couponId, quantity);
    }

    @Override
    public List<CouponEntity> findCouponByNotExpired() {
        return couponJpaRepository.findCouponByNotExpired();
    }

    @Override
    public Long issueCoupon(Long couponId, Long userId) {
         return cacheRepository.issueCoupon(couponId, userId);
    }

    @Override
    public void enterQueue(Long couponId, Long userId) {
        cacheRepository.enterQueue(couponId, userId);
    }

    @Override
    public List<String> pullQueueCoupon(Long couponId, Long count) {
        return cacheRepository.pullQueueCoupon(couponId, count);
    }
}
