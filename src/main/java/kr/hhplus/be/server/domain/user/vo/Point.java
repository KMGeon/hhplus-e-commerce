package kr.hhplus.be.server.domain.user.vo;


import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Point {
    private final long amount;

    protected Point() {
        this.amount = 0;
    }

    private Point(long amount) {
        this.amount = amount;
    }

    public static Point zero() {
        return new Point(0);
    }

    public static Point of(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("포인트는 음수가 될 수 없습니다");
        }
        return new Point(amount);
    }

    public Point add(long amount) {
        return new Point(this.amount + amount);
    }

    public Point subtract(long amount) {
        if (this.amount < amount) {
            throw new IllegalArgumentException("차감할 포인트가 보유 포인트보다 많습니다");
        }
        return new Point(this.amount - amount);
    }

}