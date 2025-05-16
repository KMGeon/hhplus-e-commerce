package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public CouponInfo.CreateInfo save(CouponCommand.Create command) {
        CouponEntity coupon = CouponEntity.createCoupon(
                command.couponName(),
                command.discountType(),
                command.initQuantity(),
                command.discountAmount(),
                LocalDateTime.now()
        );
        CouponEntity saveCoupon = couponRepository.save(coupon);
        couponRepository.initializeCoupon(saveCoupon.getId(), command.initQuantity());
        return CouponInfo.CreateInfo.of(saveCoupon.getId());
    }

    @Transactional
    public Long publishCoupon(CouponCriteria.PublishCriteria criteria) {
        couponRepository.issueCoupon(criteria.couponId(), criteria.userId());
        couponRepository.enterQueue(criteria.couponId(), criteria.userId());
        return criteria.couponId();
    }

    public List<CouponInfo.CouponAvailable> processBatchInsert() {
        List<CouponEntity> availableCoupons = couponRepository.findCouponByNotExpired();
        List<CouponInfo.CouponAvailable> results = new ArrayList<>();

        for (CouponEntity coupon : availableCoupons) {
            results.add(CouponInfo.CouponAvailable.of(
                    coupon.getId(),
                    couponRepository.pullQueueCoupon(coupon.getId(), coupon.getInitQuantity()))
            );
        }

        return results;
    }

    public void decreaseCouponQuantity(Long couponId, int decreaseQuantity) {
        couponRepository.findCouponById(couponId)
                .decreaseRemainQuantity(decreaseQuantity);
    }

    public BigDecimal calculateDiscountAmount(Long couponId, BigDecimal totalPrice) {
        return couponRepository.findCouponById(couponId)
                .calculateDiscountAmount(totalPrice);
    }

}
