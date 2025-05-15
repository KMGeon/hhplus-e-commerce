package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponScheduler {
    private final CouponFacadeService couponFacadeService;


    @Scheduled(fixedDelay = 10000)
    public void republishUnpublishedEvents() {
        couponFacadeService.processBatchExecute();
    }
}
