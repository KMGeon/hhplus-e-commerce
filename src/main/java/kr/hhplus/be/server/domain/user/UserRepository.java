package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserRepository {
    UserEntity findById(Long userId);
    UserEntity save(UserEntity user);
}