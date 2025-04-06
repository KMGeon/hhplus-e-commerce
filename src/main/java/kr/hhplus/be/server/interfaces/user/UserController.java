package kr.hhplus.be.server.interfaces.user;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.user.UserChargeCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.interfaces.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     *
     * @param userId
     * @return 유저의 포인트를 반환합니다.
     */
    @GetMapping("/point")
    public ApiResponse<UserResponseDTO.UserPointResponse> getUserPoint(
            @RequestParam(value = "userId") String userId
    ) {
        return ApiResponse.success(null);
    }

    /**
     *
     * @param chargeUserPointRequest
     * @return 유저의 포인트를 충전합니다.
     */
    @PostMapping("/point")
    public ApiResponse<Integer> chargeUserPoint(
            @Valid @RequestBody UserRequestDTO.ChargeUserPointRequest chargeUserPointRequest
            ) {
        UserChargeCommand userCommand = new UserChargeCommand(
                chargeUserPointRequest.userId(),
                chargeUserPointRequest.amount()
        );
        return ApiResponse.maskToInteger(userService.charge(userCommand));
    }
}
