package kr.hhplus.be.server.support;

import kr.hhplus.be.server.domain.support.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.List;


@Aspect
@Component
@Order(1)
public class DistributedLockAspect {
    private static final String LOCK_PREFIX = "lock:";
    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();

    public DistributedLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(distributedLock)")
    public Object executeWithLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        StandardEvaluationContext context = new StandardEvaluationContext();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        List<String> keys = parser.parseExpression(distributedLock.key()).getValue(context, List.class);

        List<RLock> locks = keys.stream()
                .map(key -> LOCK_PREFIX + key)
                .map(redissonClient::getLock)
                .toList();

        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));
        try {
            boolean isLocked = multiLock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );

            if (!isLocked) throw new RuntimeException("LOCK_ACQUISITION_FAILED");

            return joinPoint.proceed();
        } finally {
            multiLock.unlock();
        }
    }
}