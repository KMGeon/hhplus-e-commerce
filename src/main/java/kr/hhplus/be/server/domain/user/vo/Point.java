package kr.hhplus.be.server.domain.user.vo;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Point {
    private static final long MAX_LIMIT_POINT = 1_000_000L;
    private static final long ZERO = 0L;

    private final long amount;

    protected Point() {
        this.amount = ZERO;
    }

    private Point(long amount) {
        this.amount = amount;
    }

    public static Point zero() {
        return new Point(ZERO);
    }

    public static Point of(long amount) {
        validateNonNegative(amount, "포인트는 음수가 될 수 없습니다");
        return new Point(amount);
    }

    public Point add(long amount) {
        if (amount == ZERO) {
            return this;
        }

        validateNonNegative(amount, "추가할 포인트는 양수여야 합니다");

        long newAmount = this.amount + amount;
        validateNotExceedLimit(newAmount);

        return new Point(newAmount);
    }

    public Point subtract(long amount) {
        if (amount == ZERO) {
            return this;
        }

        validateNonNegative(amount, "차감할 포인트는 양수여야 합니다");
        validateSufficientBalance(amount);

        return new Point(this.amount - amount);
    }

    /**
     * 최대 한도 초과 여부 검증
     */
    private void validateNotExceedLimit(long amount) {
        if (amount > MAX_LIMIT_POINT) {
            throw new IllegalArgumentException(
                    String.format("포인트는 최대 %d을 초과할 수 없습니다", MAX_LIMIT_POINT)
            );
        }
    }

    /**
     * 포인트 잔액 충분 여부 검증
     */
    private void validateSufficientBalance(long amount) {
        if (this.amount < amount) throw new IllegalArgumentException("차감할 포인트가 보유 포인트보다 많습니다");
    }

    /**
     * 음수가 아닌지 검증
     */
    private static void validateNonNegative(long amount, String message) {
        if (amount < ZERO) throw new IllegalArgumentException(message);
    }
}