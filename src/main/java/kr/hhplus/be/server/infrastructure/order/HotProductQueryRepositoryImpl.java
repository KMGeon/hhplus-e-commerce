package kr.hhplus.be.server.infrastructure.order;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.domain.order.QOrderEntity.orderEntity;
import static kr.hhplus.be.server.domain.order.QOrderItemEntity.orderItemEntity;
import static kr.hhplus.be.server.domain.product.QProductEntity.productEntity;

@Repository
@RequiredArgsConstructor
public class HotProductQueryRepositoryImpl implements HotProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * @deprecated : redis로 인기 Product를 탐색하고 나중에 redis 장애가 발생하면 사용할 쿼리
     */
    @Override
    public List<HotProductQuery> findHotProducts(String startPath, String endPath) {
        return queryFactory
                .select(Projections.constructor(HotProductQuery.class,
                        orderItemEntity.skuId,
                        productEntity.category,
                        productEntity.productName,
                        orderItemEntity.ea.sum()))
                .from(orderEntity)
                .innerJoin(orderItemEntity).on(orderEntity.id.eq(orderItemEntity.order.id))
                .innerJoin(productEntity).on(orderItemEntity.skuId.eq(productEntity.skuId))
                .where(orderEntity.datePath.between(startPath, endPath))
                .groupBy(orderItemEntity.skuId, productEntity.category, productEntity.productName)
                .orderBy(orderItemEntity.ea.sum().desc())
                .limit(5)
                .fetch();
    }
}