package kr.hhplus.be.server.controller.user;

import jakarta.validation.Valid;
import kr.hhplus.be.server.config.common.ApiResponse;
import kr.hhplus.be.server.controller.user.dto.request.ChargeUserPointRequest;
import kr.hhplus.be.server.controller.user.dto.response.UserPointResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    /**
     *
     * @param userId
     * @return 유저의 포인트를 반환합니다.
     */
    @GetMapping("/point")
    public ApiResponse<UserPointResponse> getUserPoint(
            @RequestParam(value = "userId") String userId
    ) {
        return ApiResponse.success(new UserPointResponse(100));
    }

    /**
     *
     * @param chargeUserPointRequest
     * @return 유저의 포인트를 충전합니다.
     */
    @PostMapping("/point")
    public ApiResponse<Integer> chargeUserPoint(
            @Valid @RequestBody ChargeUserPointRequest chargeUserPointRequest
            ) {
        return ApiResponse.maskToInteger(new UserPointResponse(chargeUserPointRequest.amount()));
    }
}
