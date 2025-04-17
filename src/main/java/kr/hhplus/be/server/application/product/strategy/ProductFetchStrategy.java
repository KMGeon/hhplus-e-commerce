package kr.hhplus.be.server.application.product.strategy;

import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;

import java.util.List;

public interface ProductFetchStrategy {
    List<ProductStockDTO> fetch();
}