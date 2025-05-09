package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.config.ApplicationContext;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AopSequenceTest extends ApplicationContext {
    @Autowired
    private CouponService couponService;

    @Autowired
    private ExecutionOrderRecorder recorder;

    @TestConfiguration
    @EnableAspectJAutoProxy
    static class TestConfig {

        @Bean
        public ExecutionOrderRecorder executionOrderRecorder() {
            return new ExecutionOrderRecorder();
        }

        @Bean
        public ExecutionOrderAspect executionOrderAspect(ExecutionOrderRecorder recorder) {
            return new ExecutionOrderAspect(recorder);
        }
    }

    @BeforeEach
    void setUp() {
        recorder.clear();
    }

    @Test
    void 분산락과_트랜잭션_실행_순서_검증() {
        // when
        couponService.decreaseCouponQuantityAfterCheckLock(1L);

        // then
        List<String> executionOrder = recorder.getExecutionOrder();
        // 실행 순서 검증
        assertEquals("LOCK_ACQUIRED", executionOrder.get(0));
        assertEquals("TRANSACTION_START", executionOrder.get(1));
        assertEquals("TRANSACTION_END", executionOrder.get(2));
        assertEquals("LOCK_RELEASED", executionOrder.get(3));
    }
}
