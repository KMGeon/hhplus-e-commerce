package kr.hhplus.be.server.application.product.strategy;

import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;

import java.util.List;

public interface ProductFetchStrategy {
    List<ProductInfo.ProductInfoResponse> fetch();
}