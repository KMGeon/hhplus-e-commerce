package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    @Query(nativeQuery = true, value = """
            SELECT a.order_id FROM orders as a 
            WHERE a.expire_time < NOW() 
            AND a.status = 'CONFIRMED'
            """)
    List<Long> findExpiredOrderIds();

    @Modifying
    @Query("""
        UPDATE OrderEntity o
        SET o.status = 'CANCELLED'
        WHERE o.id IN :orderIds
        """)
    int updateOrderStatusByIds(@Param("orderIds") List<Long> orderIds);

    @Query(value = "select o from OrderEntity o where now() > o.expireTime and o.status = 'CONFIRMED'")
    List<OrderEntity> findOrderEntityByExpireTime();
}
