package kr.hhplus.be.server.domain.user.userCoupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public Long userCouponPublish(Long couponId, Long userId) {
        return userCouponRepository.save(UserCouponEntity.publishCoupon(userId, couponId))
                .getId();
    }

    public Long checkUserCoupon(Long userCouponId, Long userId) {
        UserCouponEntity getUserCoupon = userCouponRepository.findById(userCouponId);
        getUserCoupon.checkThisCouponCanUse(userId);
        return getUserCoupon.getCouponId();
    }

    public void useCoupon(Long userCouponId, long orderId) {
        userCouponRepository.findById(userCouponId)
                .use(orderId);
    }
}