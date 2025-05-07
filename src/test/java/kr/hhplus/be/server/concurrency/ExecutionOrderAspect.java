package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.support.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Order(0)
class ExecutionOrderAspect {

    private final ExecutionOrderRecorder recorder;

    public ExecutionOrderAspect(ExecutionOrderRecorder recorder) {
        this.recorder = recorder;
    }
    @Around("@annotation(distributedLock)")
    public Object aroundDistributedLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        try {
            recorder.recordEvent("LOCK_ACQUIRED");
            return joinPoint.proceed();
        } finally {
            recorder.recordEvent("LOCK_RELEASED");
        }
    }

    @Around("@annotation(transactional)")
    public Object aroundTransactional(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        try {
            recorder.recordEvent("TRANSACTION_START");
            return joinPoint.proceed();
        } finally {
            recorder.recordEvent("TRANSACTION_END");
        }
    }

}
