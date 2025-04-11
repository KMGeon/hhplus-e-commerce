package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacadeService;
import kr.hhplus.be.server.domain.product.CategoryEnum;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.interfaces.common.ApiResponse;
import kr.hhplus.be.server.interfaces.product.response.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductFacadeService productFacadeService;

    public ProductController(ProductFacadeService productFacadeService) {
        this.productFacadeService = productFacadeService;
    }

    @GetMapping("/product")
    public ApiResponse<List<ProductInfo.ProductInfoResponse>> getProducts(
            @RequestParam(value = "category", required = false) String category
    ) {
        return ApiResponse.success(productFacadeService.getProducts(category));
    }

    @GetMapping("/hot-product")
    public ApiResponse<List<ProductResponse>> getHotProducts() {
        return ApiResponse.success(mockPorudctList(null));
    }

    private List<ProductResponse> mockPorudctList(String category) {
        if (category != null && !category.isEmpty()) {
            return List.of(new ProductResponse(1, "카테고리_" + category + "_상품", 1000, 1));
        } else {
            return List.of(
                    new ProductResponse(1, "상품1", 1000, 1),
                    new ProductResponse(2, "상품2", 2000, 2),
                    new ProductResponse(3, "상품3", 3000, 3)
            );
        }
    }
}
