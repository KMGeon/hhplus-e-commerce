package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.projection.HotProductQuery;

import java.util.List;

public interface HotProductQueryRepository {
    List<HotProductQuery> findHotProducts(String startPath, String endPath);
}
