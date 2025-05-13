package kr.hhplus.be.server.interfaces.user;

import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserInfo;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController implements UserControllerDocs {


    private final UserService userService;

    /**
     * @param userId
     * @return 유저의 포인트를 반환합니다.
     */
    @GetMapping("/point")
    public ApiResponse<UserResponse.User> getUserPoint(
            @RequestParam(required = true, value = "userId") Long userId
    ) {
        UserInfo.User user = userService.getUser(userId);
        return ApiResponse.success(UserResponse.User.toResponse(user));
    }

    /**
     * @param chargeUserPointRequest
     * @return 유저의 포인트를 충전합니다.
     */
    @PostMapping("/point")
    public ApiResponse<UserResponse.User> chargeUserPoint(
            @Valid @RequestBody UserRequest.ChargeUserPointRequest chargeUserPointRequest
    ) {
        UserCommand.PointCharge pointCommand = chargeUserPointRequest.toPointCommand();
        UserInfo.User chargedUser = userService.charge(pointCommand);
        return ApiResponse.success(UserResponse.User.toResponse(chargedUser));
    }
}
