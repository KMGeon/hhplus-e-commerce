package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findById(Long id);
    UserEntity save(UserEntity user);
    UserEntity update(UserEntity user);
}