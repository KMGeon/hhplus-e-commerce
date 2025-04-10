package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.vo.Point;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Entity(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Point point;


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

    public void usePoint(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("사용 금액은 양수여야 합니다");
        this.point = this.point.decreasePoint(amount);
    }

    public long getPoint(){
        return this.point.getAmount();
    }
}




