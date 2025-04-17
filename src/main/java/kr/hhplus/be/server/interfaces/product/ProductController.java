package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacadeService;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import kr.hhplus.be.server.interfaces.product.response.ProductResponse;
import kr.hhplus.be.server.support.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacadeService productFacadeService;


    /** 상품조회 **/
    @GetMapping("/api/v1/product")
    public ApiResponse<List<ProductStockDTO>> getProducts(
            @RequestParam(value = "category", required = false) String category
    ) {
        return ApiResponse.success(productFacadeService.getProducts(category));
    }

    /** 인기 상품 조회 **/
    @GetMapping("/hot-product")
    public ApiResponse<List<ProductResponse>> getHotProducts() {
        return ApiResponse.success(null);
    }
}

/**
 {
 "code": "",
 "message": "",
 "data": [
 {
 "skuId": "SM-S24-ULTRA",
 "unitPrice": 1450000,
 "category": "SAMSUNG",
 "productName": "Galaxy S24 Ultra",
 "productId": 4,
 "stockEa": 13
 },
 {
 "skuId": "SM-TAB-S9",
 "unitPrice": 950000,
 "category": "SAMSUNG",
 "productName": "Galaxy Tab S9",
 "productId": 5,
 "stockEa": 7
 },
 {
 "skuId": "SM-BOOK-PRO",
 "unitPrice": 1650000,
 "category": "SAMSUNG",
 "productName": "Galaxy Book Pro",
 "productId": 6,
 "stockEa": 4
 }
 ]
 }
 */