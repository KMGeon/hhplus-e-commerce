package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    @Query(nativeQuery = true, value = """
            SELECT id FROM orders 
            WHERE expire_time < NOW() 
            AND status = 'CONFIRMED'
            """)
    List<Long> findExpiredOrderIds();

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE orders
            SET status = 'CANCELLED',
                updated_at = NOW()
            WHERE id IN :ids
            """)
    long updateOrderStatusByIds(@Param("ids") List<Long> ids);

    @Query(value = "select o from OrderEntity o where now() > o.expireTime and o.status = 'CONFIRMED'")
    List<OrderEntity> findOrderEntityByExpireTime();
}
