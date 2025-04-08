package kr.hhplus.be.server.application.product.strategy;

import kr.hhplus.be.server.domain.product.ProductEntity;

import java.util.List;

public interface ProductFetchStrategy {
    List<ProductEntity> fetch();
}