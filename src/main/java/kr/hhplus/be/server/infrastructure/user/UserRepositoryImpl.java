package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserRepositoryImpl  implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserEntity findById(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(String.format("회원을 찾을 수 없습니다. id: %s", userId)));
    }

    @Override
    public UserEntity findByIdOptimisticLock(Long userId) {
        return userJpaRepository.findByIdOptimisticLock(userId)
                .orElseThrow(()-> new IllegalArgumentException(String.format("회원을 찾을 수 없습니다. id: %s", userId)));
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userJpaRepository.save(user);
    }
}
