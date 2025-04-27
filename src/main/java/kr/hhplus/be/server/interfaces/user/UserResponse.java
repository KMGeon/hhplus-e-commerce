package kr.hhplus.be.server.interfaces.user;

import kr.hhplus.be.server.domain.user.UserInfo;

public class UserResponse {
    public record User(Long userId, Long amount) {
        public static User toResponse(UserInfo.User userInfo) {
            return new User(userInfo.userId(), userInfo.amount());
        }
    }
}
