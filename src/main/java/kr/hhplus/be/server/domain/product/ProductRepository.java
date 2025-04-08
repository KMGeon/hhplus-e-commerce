package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<ProductEntity> findById(Long id);
    List<ProductEntity> findAll();
    List<ProductEntity> findAllByCategory(char category);
}