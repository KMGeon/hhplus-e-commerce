package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl  implements UserRepository {

    private final UserJpaFakeRepository userJpaFakeRepository;

    @Override
    public Optional<UserEntity> findById(Long userId) {
        return userJpaFakeRepository.findById(userId);
    }

    @Override
    public UserEntity update(UserEntity userEntity) {
        return userJpaFakeRepository.update(userEntity);
    }
}
