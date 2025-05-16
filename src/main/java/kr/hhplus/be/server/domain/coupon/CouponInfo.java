package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CouponInfo {
    public record CreateInfo(Long couponId){
        public static CreateInfo of(Long couponId) {
            return new CreateInfo(couponId);
        }
    }

    public record CouponAvailable(Long couponId, List<Long> userIds) {
        public static CouponAvailable of(Long couponId, List<String> stringUserIds) {
            List<Long> userIds = stringUserIds.stream()
                    .filter(Objects::nonNull)
                    .filter(id -> !id.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            return new CouponAvailable(couponId, userIds);
        }
    }
}
