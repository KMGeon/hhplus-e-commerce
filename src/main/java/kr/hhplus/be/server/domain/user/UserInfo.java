package kr.hhplus.be.server.domain.user;

public class UserInfo {
    public record UserChargeInfo (Long userId, Long amount){
        public static UserChargeInfo from(UserEntity userEntity){
            return new UserChargeInfo(userEntity.getId(), userEntity.getPoint());
        }
    }
}
