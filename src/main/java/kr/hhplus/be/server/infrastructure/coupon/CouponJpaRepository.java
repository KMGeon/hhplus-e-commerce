package kr.hhplus.be.server.infrastructure.coupon;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<CouponEntity , Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from coupon c where c.id = :id")
    Optional<CouponEntity> findCouponByIdWithPessimisticLock(@Param("id") Long id);

}
