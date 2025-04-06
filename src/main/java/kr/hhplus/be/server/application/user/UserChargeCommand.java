package kr.hhplus.be.server.application.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChargeCommand {
    private Long userId;
    private Long amount;

    public UserChargeCommand(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
