package kr.hhplus.be.server.interfaces.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponService couponService;


    @Test
    @DisplayName("쿠폰 생성 API 테스트")
    void createCouponTest() throws Exception {
        // given
        CouponRequest.Create createRequest = new CouponRequest.Create(
                "10% 할인 쿠폰", "PERCENTAGE", 100L, 10.0);

        Long couponId = 1L;
        CouponInfo.CreateInfo couponInfo = CouponInfo.CreateInfo.of(couponId);

        given(couponService.save(any(CouponCommand.Create.class))).willReturn(couponInfo);

        // when & then
        mockMvc.perform(post("/api/v1/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.couponId").value(couponId));

        verify(couponService).save(any(CouponCommand.Create.class));
    }

    @Test
    @DisplayName("쿠폰 발행 API 테스트")
    void publishCouponTest() throws Exception {
        // given
        long userId = 1L;
        long couponId = 100L;
        CouponRequest.Publish publishRequest = new CouponRequest.Publish(userId, couponId);

        given(couponService.publishCoupon(any(CouponCommand.Publish.class))).willReturn(userId);

        // when & then
        mockMvc.perform(post("/api/v1/coupon/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publishRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(userId));

        verify(couponService).publishCoupon(any(CouponCommand.Publish.class));
    }

    @Test
    @DisplayName("쿠폰 발행 API 예외 테스트 - 서비스 레이어 예외")
    void publishCouponServiceExceptionTest() throws Exception {
        // given
        long userId = 1L;
        long couponId = 100L;
        CouponRequest.Publish publishRequest = new CouponRequest.Publish(userId, couponId);

        String errorMessage = "쿠폰 발행 중 오류가 발생했습니다";
        given(couponService.publishCoupon(any(CouponCommand.Publish.class)))
                .willThrow(new RuntimeException(errorMessage));

        // when & then
        mockMvc.perform(post("/api/v1/coupon/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publishRequest)))
                .andExpect(status().isInternalServerError());
    }
}