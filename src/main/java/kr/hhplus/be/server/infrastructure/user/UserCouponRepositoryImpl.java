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
    public Optional<UserCouponEntity> findById(long userCouponId) {
        return repository.findById(userCouponId);
    }

    @Override
    public Optional<UserCouponEntity> findByIdWithCoupon(long userCouponId) {
        return repository.findByIdWithCoupon(userCouponId);
    }

    @Override
    public boolean existsCoupon(long userId, long couponId) {
        return repository.existsByUserIdAndCouponId(userId, couponId);
    }
}
