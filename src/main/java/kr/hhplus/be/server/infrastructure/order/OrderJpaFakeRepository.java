package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderJpaFakeRepository {

    public OrderEntity save(OrderEntity order) {
        return null;
    }
}
