package kr.hhplus.be.server.interfaces.coupon;

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

@Tag(name = "coupon", description = "쿠폰 API")
public interface CouponControllerDocs {

    @Operation(
            summary = "쿠폰 생성",
            description = "새로운 쿠폰을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CouponResponse.CreateCouponResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": "SUCCESS",
                                              "message": null,
                                              "data": {
                                                "couponId": 1,
                                                "name": "신규 가입 쿠폰",
                                                "discountType": "FIXED_AMOUNT",
                                                "discountValue": 5000,
                                                "createdAt": "2024-07-01T14:30:00"
                                              }
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
                                                "message": "쿠폰 정보가 올바르지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.support.ApiResponse<CouponResponse.CreateCouponResponse> createCoupon(
            @Parameter(
                    description = "쿠폰 생성 요청 정보",
                    required = true,
                    schema = @Schema(implementation = CouponRequest.Create.class)
            ) CouponRequest.Create createRequest
    );

    @Operation(
            summary = "쿠폰 발행",
            description = "생성된 쿠폰을 사용자에게 발행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 발행 성공",
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
                                                "message": "쿠폰 발행 정보가 올바르지 않습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "쿠폰 또는 사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "code": "NOT_FOUND",
                                                "message": "해당 쿠폰을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "쿠폰 발행 충돌 (이미 발행됨)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "code": "CONFLICT",
                                                "message": "이미 발행된 쿠폰입니다."
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.support.ApiResponse<Long> publishCoupon(
            @Valid @Parameter(
                    description = "쿠폰 발행 요청 정보",
                    required = true,
                    schema = @Schema(implementation = CouponRequest.Publish.class)
            ) CouponRequest.Publish publishRequest
    );
} 