package kr.hhplus.be.server.application.payment;

public class PaymentCriteria {
    public record Pay(long userId, long orderId, Long userCouponId){}
}
