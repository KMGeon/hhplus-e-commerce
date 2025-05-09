package kr.hhplus.be.server.interfaces.coupon;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.support.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CouponController implements CouponControllerDocs {

    private final CouponService couponService;
    private final CouponFacadeService couponFacadeService;


    @PostMapping("/coupon")
    public ApiResponse<CouponResponse.CreateCouponResponse> createCoupon(
            @RequestBody CouponRequest.Create createRequest
    ) {
        CouponInfo.CreateInfo couponInfo = couponService.save(createRequest.toCommand());
        return ApiResponse.success(CouponResponse.CreateCouponResponse.of(couponInfo));
    }

    @PostMapping("/coupon/publish")
    public ApiResponse<Long> publishCoupon(
            @Valid @RequestBody CouponRequest.Publish publishRequest
    ) {
        CouponCriteria.PublishCriteria criteria = publishRequest.toCriteria();
        return ApiResponse.success(couponFacadeService.publishCoupon(criteria));
    }

}
