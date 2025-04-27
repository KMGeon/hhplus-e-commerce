package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.userCoupon.UserCouponEntity;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository repository;

    @Override
    public UserCouponEntity save(UserCouponEntity userCouponEntity) {
        return repository.save(userCouponEntity);
    }

    @Override
    public UserCouponEntity findByUserIdAndCouponId(long userId, long couponId) {
        return repository.findByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public UserCouponEntity findById(long userCouponId) {
        return repository.findById(userCouponId)
                .orElseThrow(()-> new IllegalArgumentException("사용자 쿠폰을 찾을 수 없습니다."));
    }

}
