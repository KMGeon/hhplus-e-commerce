//package kr.hhplus.be.server.interfaces.coupon;
//
//import kr.hhplus.be.server.config.AbstractRestDocsTests;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
//
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
//import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//public class CouponControllerTest extends AbstractRestDocsTests {
//
//    private final String USER_ID = "1";
//
//    @Test
//    @DisplayName("유저가 보유한 쿠폰 목록 조회")
//    void getUserCouponList() throws Exception {
//        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/coupon")
//                        .param("userId", USER_ID)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.length()").value(3))
//                .andExpect(jsonPath("$.data[0].couponName").value("쿠폰1"))
//                .andDo(document("get-user-coupon-list",
//                        queryParameters(
//                                parameterWithName("userId").description("유저 ID")
//                        ),
//                        responseFields(
//                                fieldWithPath("code").description("응답 코드"),
//                                fieldWithPath("message").description("응답 메시지"),
//                                fieldWithPath("data").description("유저 보유 쿠폰 목록"),
//                                fieldWithPath("data[].id").type("NUMBER").description("쿠폰 ID"),
//                                fieldWithPath("data[].couponName").type("STRING").description("쿠폰명"),
//                                fieldWithPath("data[].discountPercentage").type("NUMBER").description("할인율"),
//                                fieldWithPath("data[].expirationDate").type("STRING").description("만료일")
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("쿠폰 목록 조회")
//    void getCouponList() throws Exception {
//        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/coupons")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.length()").value(3))
//                .andExpect(jsonPath("$.data[0].couponName").value("쿠폰1"))
//                .andDo(document("get-coupon-list",
//                        responseFields(
//                                fieldWithPath("code").description("응답 코드"),
//                                fieldWithPath("message").description("응답 메시지"),
//                                fieldWithPath("data").description("쿠폰 목록"),
//                                fieldWithPath("data[].id").type("NUMBER").description("쿠폰 ID"),
//                                fieldWithPath("data[].couponName").type("STRING").description("쿠폰명"),
//                                fieldWithPath("data[].discountPercentage").type("NUMBER").description("할인율"),
//                                fieldWithPath("data[].expirationDate").type("STRING").description("만료일")
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("쿠폰 발행")
//    void createCoupon() throws Exception {
//        String requestBody = """
//        {
//            "couponId": 1,
//            "userId": 1
//        }
//        """;
//
//        mockMvc.perform(post("/api/v1/coupon")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value(1))
//                .andDo(document("create-coupon",
//                        requestFields(
//                                fieldWithPath("couponId").type("NUMBER").description("쿠폰 ID"),
//                                fieldWithPath("userId").type("NUMBER").description("사용자 ID")
//                        ),
//                        responseFields(
//                                fieldWithPath("code").description("응답 코드"),
//                                fieldWithPath("message").description("응답 메시지"),
//                                fieldWithPath("data").description("발행된 쿠폰 ID")
//                        )
//                ));
//    }
//}