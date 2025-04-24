package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.ErrorResponse;

@Tag(name = "order", description = "주문 API")
public interface OrderControllerDocs {

    @Operation(
            summary = "주문 생성",
            description = "새로운 상품 주문을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": "SUCCESS",
                                              "message": null,
                                              "data": 1001
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "code": "BAD_REQUEST",
                                                "message": "주문 정보가 올바르지 않습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "code": "NOT_FOUND",
                                                "message": "해당 상품을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "재고 부족",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "code": "CONFLICT",
                                                "message": "상품 재고가 부족합니다."
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.support.ApiResponse<Long> createOrder(
            @Valid @Parameter(
                    description = "주문 생성 요청 정보",
                    required = true,
                    schema = @Schema(implementation = OrderRequestDTO.CreateOrderRequest.class)
            ) OrderRequestDTO.CreateOrderRequest request
    );
} 