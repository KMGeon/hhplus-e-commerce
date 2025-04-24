package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void usePoint(long userId, BigDecimal finalTotalPrice) {
        UserEntity getUser = userRepository.findByIdOptimisticLock(userId);
        getUser.usePoint(finalTotalPrice.longValue());
    }
}