package kr.hhplus.be.server.domain.user;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfo.User getUser(long userId) {
        UserEntity getUser = userRepository.findById(userId);
        return UserInfo.User.from(getUser);
    }

    @Transactional
    public UserInfo.User charge(UserCommand.PointCharge userChargeCommand) {
        UserEntity rtn = userRepository.findById(userChargeCommand.userId())
                .chargePoint(userChargeCommand.amount());
        return UserInfo.User.from(rtn);
    }

    @Retryable(
            retryFor = {
                    OptimisticLockException.class,
                    ObjectOptimisticLockingFailureException.class
            },
            maxAttempts = 10,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void usePoint(long userId, BigDecimal finalTotalPrice) {
        UserEntity getUser = userRepository.findByIdOptimisticLock(userId);
        getUser.usePoint(finalTotalPrice.longValue());
    }

    @Recover
    public void recoverFromOptimisticLockingFailure(Exception e, long userId, BigDecimal amount) {
        log.error("포인트 차감 실패 - 최대 재시도 초과: userId={}, amount={}, 원인={}", userId, amount, e.getMessage());
        throw new IllegalStateException("포인트 차감 실패 - 최종 실패", e);
    }
}