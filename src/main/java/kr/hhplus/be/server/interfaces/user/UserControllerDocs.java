package kr.hhplus.be.server.interfaces.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.ErrorResponse;

@Tag(name = "user", description = "사용자 API")
public interface UserControllerDocs {

    @Operation(
            summary = "사용자 포인트 조회",
            description = "사용자의 ID를 기반으로 현재 보유한 포인트 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "포인트 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.User.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "userId": 1,
                                                "point": 10000,
                                                "updatedAt": "2024-07-01T15:30:45"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "code": "NOT_FOUND",
                                                "message": "사용자를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.interfaces.ApiResponse<UserResponse.User> getUserPoint(
            @Parameter(
                    description = "조회할 사용자의 ID",
                    required = true
            ) Long userId
    );

    @Operation(
            summary = "사용자 포인트 충전",
            description = "사용자의 포인트를 지정된 금액만큼 충전합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "포인트 충전 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.User.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "userId": 1,
                                                "point": 15000,
                                                "updatedAt": "2024-07-01T15:35:22"
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
                                                "message": "충전 금액은 0보다 커야 합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "code": "NOT_FOUND",
                                                "message": "사용자를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    kr.hhplus.be.server.interfaces.ApiResponse<UserResponse.User> chargeUserPoint(
            @Parameter(
                    description = "포인트 충전 요청 정보",
                    required = true
            ) UserRequest.ChargeUserPointRequest chargeUserPointRequest
    );
} 