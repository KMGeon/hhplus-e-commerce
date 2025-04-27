package kr.hhplus.be.server.infrastructure.user;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    /** 낙관적 락 사용 **/
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select u from user u where u.id = :id")
    Optional<UserEntity> findByIdOptimisticLock(@Param("id") Long id);
}
