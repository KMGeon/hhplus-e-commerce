package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.order.projection.HotProductQuery;
import kr.hhplus.be.server.domain.product.projection.ProductStockDTO;
import org.springframework.data.domain.Page;

import java.util.List;

@Tag(name = "product", description = "상품 API")
public interface ProductControllerDocs {

    @Operation(
            summary = "상품 목록 조회",
            description = "카테고리별 상품 목록을 페이징하여 조회합니다. 카테고리가 지정되지 않은 경우 전체 상품을 조회합니다."
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
                                                "data": {
                                                    "content": [
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
                                                    ],
                                                    "pageable": {
                                                        "sort": {
                                                            "empty": true,
                                                            "sorted": false,
                                                            "unsorted": true
                                                        },
                                                        "offset": 0,
                                                        "pageNumber": 0,
                                                        "pageSize": 10,
                                                        "paged": true,
                                                        "unpaged": false
                                                    },
                                                    "last": false,
                                                    "totalPages": 5,
                                                    "totalElements": 42,
                                                    "size": 10,
                                                    "number": 0,
                                                    "sort": {
                                                        "empty": true,
                                                        "sorted": false,
                                                        "unsorted": true
                                                    },
                                                    "first": true,
                                                    "numberOfElements": 10,
                                                    "empty": false
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.interfaces.ApiResponse<Page<ProductStockDTO>> getProducts(
            @Parameter(
                    description = "조회할 상품 카테고리 (미입력 시 전체 상품 조회)",
                    required = false
            ) String category,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    required = false,
                    example = "0"
            ) int page,
            @Parameter(
                    description = "페이지 크기",
                    required = false,
                    example = "10"
            ) int size
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
                            schema = @Schema(implementation = HotProductQuery.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "SUCCESS",
                                                "message": null,
                                                "data": [
                                                    {
                                                        "skuId": "SKU-003",
                                                        "category": "과자",
                                                        "productName": "초코과자",
                                                        "orderCount": 120
                                                    },
                                                    {
                                                        "skuId": "SKU-005",
                                                        "category": "디저트",
                                                        "productName": "아이스크림",
                                                        "orderCount": 98
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.interfaces.ApiResponse<List<HotProductQuery>> getHotProducts();
}