package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserJpaFakeRepository {
    Optional<UserEntity> findById(Long userId){
        return Optional.empty();
    }

    UserEntity update(UserEntity userEntity){
        return null;
    }

}
