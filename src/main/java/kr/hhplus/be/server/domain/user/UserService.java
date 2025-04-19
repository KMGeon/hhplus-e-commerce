package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.order.OrderCoreRepository;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final OrderCoreRepository orderCoreRepository;

    @Transactional(readOnly = true)
    public Long getUserPoint(long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));
        return user.getPoint();
    }

    @Transactional(readOnly = true)
    public long validateUserForCoupon(long userId, long couponId) {
        long validatedUserId = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."))
                .getId();
        boolean hasCoupon = userCouponRepository.existsCoupon(userId, couponId);

        if (hasCoupon) throw new RuntimeException("사용자가 이미 해당 쿠폰을 보유하고 있습니다.");

        return validatedUserId;
    }

    @Transactional(readOnly = true)
    public long getUserId(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."))
                .getId();
    }

    @Transactional
    public UserInfo.UserChargeInfo charge(UserCommand.PointCharge userChargeCommand) {
        UserEntity rtn = userRepository.findById(userChargeCommand.userId())
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."))
                .chargePoint(userChargeCommand.amount());
        return UserInfo.UserChargeInfo.from(rtn);
    }

    public void payProcess(long userId, long orderId) {
        OrderEntity orderEntity = orderCoreRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문이 존재하지 않습니다."));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        userEntity.pay(orderEntity);
    }
}
