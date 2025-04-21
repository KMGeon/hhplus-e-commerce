package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfo.User getUser(final long userId) {
        return UserInfo.User.from(userRepository.findById(userId));
    }

    @Transactional
    public UserInfo.User charge(UserCommand.PointCharge userChargeCommand) {
        UserEntity rtn = userRepository.findById(userChargeCommand.userId())
                .chargePoint(userChargeCommand.amount());
        return UserInfo.User.from(rtn);
    }

    public void usePoint(long userId, BigDecimal finalTotalPrice) {
        UserEntity getUser = userRepository.findById(userId);
        getUser.usePoint(finalTotalPrice.longValue());
    }
}