package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponFacadeService {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Transactional
    public void processBatchExecute() {
        List<CouponInfo.CouponAvailable> processBatchInsert = couponService.processBatchInsert();
        for (CouponInfo.CouponAvailable couponAvailable : processBatchInsert) {
            if (couponAvailable.userIds().isEmpty())
                continue;

            couponService.decreaseCouponQuantity(
                    couponAvailable.couponId(),
                    couponAvailable.userIds().size()
            );
            userCouponService.batchPublishUserCoupon(
                    couponAvailable.couponId(),
                    couponAvailable.userIds()
            );
        }
    }
}
