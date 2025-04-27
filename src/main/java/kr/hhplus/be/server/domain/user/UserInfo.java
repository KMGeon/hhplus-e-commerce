package kr.hhplus.be.server.domain.user;

public class UserInfo {
    public record User (Long userId, Long amount){
        public static User from(UserEntity userEntity){
            return new User(userEntity.getId(), userEntity.getPoint());
        }
    }
}
