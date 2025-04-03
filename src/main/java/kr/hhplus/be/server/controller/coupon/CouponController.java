package kr.hhplus.be.server.controller.coupon;

import jakarta.validation.Valid;
import kr.hhplus.be.server.config.common.ApiResponse;
import kr.hhplus.be.server.controller.coupon.dto.request.CouponPublishRequest;
import kr.hhplus.be.server.controller.coupon.dto.response.CouponResponse;
import kr.hhplus.be.server.controller.coupon.dto.response.UserCouponResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CouponController {

    /**
     *
     * @param userId
     * @return 유저가 보유한 쿠폰 리스트
     */
    @GetMapping("/coupon")
    public ApiResponse<List<UserCouponResponse>> getUserCouponList(
            @RequestParam(value = "userId") String userId
    ) {
        List<UserCouponResponse> couponList = List.of(
                new UserCouponResponse(1L, "쿠폰1", 10.0, LocalDateTime.now().plusDays(1)),
                new UserCouponResponse(2L, "쿠폰2", 20.0, LocalDateTime.now().plusDays(2)),
                new UserCouponResponse(3L, "쿠폰3", 30.0, LocalDateTime.now().plusDays(3))
        );
        return ApiResponse.success(couponList);
    }

    /**
     *
     * @return 쿠폰 리스트 조회
     */
    @GetMapping("/coupons")
    public ApiResponse<List<CouponResponse>> getCouponList(){
        List<CouponResponse> couponList = List.of(
                new CouponResponse(1L, "쿠폰1", 10.0, LocalDateTime.now().plusDays(1)),
                new CouponResponse(2L, "쿠폰2", 20.0, LocalDateTime.now().plusDays(2)),
                new CouponResponse(3L, "쿠폰3", 30.0, LocalDateTime.now().plusDays(3))
        );
        return ApiResponse.success(couponList);
    }

    /**
     *
     * @param couponPublishRequest
     * @return 쿠폰을 발행합니다.
     */
    @PostMapping("/coupon")
    public ApiResponse<Integer> createCoupon(
            @Valid @RequestBody CouponPublishRequest couponPublishRequest
    ) {
        return ApiResponse.maskToInteger(1);
    }

}
