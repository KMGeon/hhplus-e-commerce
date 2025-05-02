package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.product.projection.HotProductDTO;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;

import java.util.List;

@Tag(name = "product", description = "상품 API")
public interface ProductControllerDocs {

    @Operation(
            summary = "상품 목록 조회",
            description = "카테고리별 상품 목록을 조회합니다. 카테고리가 지정되지 않은 경우 전체 상품을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductStockDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "SUCCESS",
                                                "message": null,
                                                "data": [
                                                    {
                                                        "id": 1,
                                                        "name": "콜라",
                                                        "price": 1500,
                                                        "category": "음료",
                                                        "description": "시원한 콜라",
                                                        "stock": 100,
                                                        "createdAt": "2024-07-01T10:00:00",
                                                        "updatedAt": "2024-07-01T10:00:00"
                                                    },
                                                    {
                                                        "id": 2,
                                                        "name": "사이다",
                                                        "price": 1500,
                                                        "category": "음료",
                                                        "description": "톡 쏘는 사이다",
                                                        "stock": 85,
                                                        "createdAt": "2024-07-01T10:00:00",
                                                        "updatedAt": "2024-07-01T10:00:00"
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.interfaces.ApiResponse<List<ProductStockDTO>> getProducts(
            @Parameter(
                    description = "조회할 상품 카테고리 (미입력 시 전체 상품 조회)",
                    required = false
            ) String category
    );

    @Operation(
            summary = "인기 상품 조회",
            description = "현재 인기있는 상품 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인기 상품 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HotProductDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "SUCCESS",
                                                "message": null,
                                                "data": [
                                                    {
                                                        "id": 3,
                                                        "name": "초코과자",
                                                        "price": 2000,
                                                        "category": "과자",
                                                        "description": "달콤한 초코과자",
                                                        "stock": 50,
                                                        "popularity": 9.5,
                                                        "reviewCount": 120,
                                                        "createdAt": "2024-07-01T10:00:00",
                                                        "updatedAt": "2024-07-01T10:00:00"
                                                    },
                                                    {
                                                        "id": 5,
                                                        "name": "아이스크림",
                                                        "price": 1800,
                                                        "category": "디저트",
                                                        "description": "부드러운 아이스크림",
                                                        "stock": 75,
                                                        "popularity": 8.9,
                                                        "reviewCount": 98,
                                                        "createdAt": "2024-07-01T10:00:00",
                                                        "updatedAt": "2024-07-01T10:00:00"
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.interfaces.ApiResponse<List<HotProductDTO>> getHotProducts();
} 