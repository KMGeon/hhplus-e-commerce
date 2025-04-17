package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
}
