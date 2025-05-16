package kr.hhplus.be.server.domain.user.userCoupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public int batchPublishUserCoupon(Long couponId, List<Long> userIds) {
        var userCoupons = new ArrayList<UserCouponEntity>();

        for (Long userId : userIds)
            userCoupons.add(UserCouponEntity.publishCoupon(userId, couponId));

        userCouponRepository.saveAll(userCoupons);
        return userIds.size();
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