package kr.hhplus.be.server.interfaces;

import kr.hhplus.be.server.domain.outbox.OutboxEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    List<OutboxEntity> findAllByCreatedAtLessThanEqualOrderByCreatedAtAsc(
            LocalDateTime createdAt,
            Pageable pageable
    );
}
