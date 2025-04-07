package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.vo.Point;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    private Long id;

    private Point point;

    /** FakeRepository Init 전용 메서드 **/
    public static UserEntity initializeUserEntity(Long id, Long point) {
        UserEntity user = new UserEntity();
        user.id = id;
        user.point = Point.of(point);
        return user;
    }

    public static UserEntity createNewUser() {
        UserEntity user = new UserEntity();
        user.point = Point.zero();
        return user;
    }

    public UserEntity chargePoint(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("충전 금액은 양수여야 합니다");
        this.point = this.point.add(amount);
        return this;
    }

}


