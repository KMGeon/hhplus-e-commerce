package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacadeService;
import kr.hhplus.be.server.domain.product.projection.HotProductDTO;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductControllerDocs {

    private final ProductFacadeService productFacadeService;

    /** 상품조회 **/
    @GetMapping("/api/v1/product")
    public ApiResponse<Page<ProductStockDTO>> getProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.success(productFacadeService.getProducts(category, page, size));
    }

    /** 인기 상품 조회 **/
    @GetMapping("/api/v1/hot-product")
    public ApiResponse<List<HotProductDTO>> getHotProducts() {
        return ApiResponse.success(productFacadeService.getHotProducts());
    }
}
