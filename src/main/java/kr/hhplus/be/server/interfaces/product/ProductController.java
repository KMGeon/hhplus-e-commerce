package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacadeService;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.domain.vo.Ranking;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductControllerDocs {

    private final ProductFacadeService productFacadeService;
    private final ProductService productService;

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
    public ApiResponse<Ranking> getHotProducts(
            @RequestParam(value = "period", defaultValue = "DAILY") String period,
            @RequestParam(value = "topNumber", defaultValue = "5") int topNumber
    ) {
        return ApiResponse.success(productService.getHotProducts(period, topNumber));
    }
}
