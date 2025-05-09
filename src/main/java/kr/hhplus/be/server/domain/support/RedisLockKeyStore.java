package kr.hhplus.be.server.domain.support;

public class RedisLockKeyStore {
    public static final String DECREASE_COUPON_LOCK = "'coupon:' + #couponId";
    public static final String DECREASE_STOCK_ORDER_LOCK = "#stockCommand.items().![skuId()]";
    private RedisLockKeyStore() {
        throw new IllegalStateException();
    }
}
