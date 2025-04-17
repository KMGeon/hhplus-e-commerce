package kr.hhplus.be.server.domain.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;


    @Transactional
    public CouponInfo.CreateInfo save(CouponCommand.Create command){
        CouponEntity coupon = CouponEntity.createCoupon(
                command.couponName(),
                command.discountType(),
                command.initQuantity(),
                command.discountAmount(),
                LocalDateTime.now()
        );
        CouponEntity saveCoupon = couponRepository.save(coupon);
        return CouponInfo.CreateInfo.of(saveCoupon.getId());
    }

    @Transactional
    public void validateAndDecreaseCoupon(final long couponId) {
        CouponEntity coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("쿠폰이 존재하지 않습니다."));

        coupon.validateForPublish();
        coupon.decreaseQuantity();
    }
}
