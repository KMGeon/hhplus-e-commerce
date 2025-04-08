package kr.hhplus.be.server.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductEntity getProduct(final Long id) {
        return productRepository.findById(1L)
                .orElseThrow(()-> new RuntimeException("해당 상품이 존재하지 않습니다."));
    }

    public List<ProductEntity> getAllProductByCategoryCode(char category) {
        return productRepository.findAllByCategory(category);
    }

    public List<ProductEntity> getAllProduct(){
        return productRepository.findAll();
    }
}
