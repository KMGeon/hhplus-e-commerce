package kr.hhplus.be.server.interfaces.user;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.support.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * @param userId
     * @return 유저의 포인트를 반환합니다.
     */
    @GetMapping("/point")
    public ApiResponse<Long> getUserPoint(
            @RequestParam(required = true, value = "userId") Long userId
    ) {
        return ApiResponse.success(userService.getUserPoint(userId));
    }

    /**
     * @param chargeUserPointRequest
     * @return 유저의 포인트를 충전합니다.
     */
    @PostMapping("/point")
    public ApiResponse<Integer> chargeUserPoint(
            @Valid @RequestBody UserRequest.ChargeUserPointRequest chargeUserPointRequest
    ) {
        UserCommand.PointCharge pointCommand = chargeUserPointRequest.toPointCommand();
        return ApiResponse.maskToInteger(userService.charge(pointCommand));
    }

}
