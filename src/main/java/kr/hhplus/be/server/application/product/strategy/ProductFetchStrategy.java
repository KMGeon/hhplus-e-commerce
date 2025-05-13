package kr.hhplus.be.server.application.product.strategy;

import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductFetchStrategy {
    Page<ProductStockDTO> fetch(int page, int size);
}