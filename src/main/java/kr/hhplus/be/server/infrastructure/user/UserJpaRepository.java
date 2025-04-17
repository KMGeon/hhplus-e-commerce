package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
