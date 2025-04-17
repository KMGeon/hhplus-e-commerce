package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findById(Long userId);
    UserEntity save(UserEntity user);
}