package kr.hhplus.be.server.domain.user.userCoupon;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public Long publishOnlyIfFirstTime(CouponCriteria.PublishCriteria criteria) {
        Long userId = criteria.userId();
        Long couponId = criteria.couponId();
        UserCouponEntity userCoupon = userCouponRepository.findByUserIdAndCouponId(userId, couponId);

        if (userCoupon != null)
            throw new RuntimeException(String.format("이미 발행된 쿠폰입니다. userId: %d, couponId: %d", userId, couponId));

        return userCouponRepository.save(UserCouponEntity.publishCoupon(userId, couponId)).getUserId();
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