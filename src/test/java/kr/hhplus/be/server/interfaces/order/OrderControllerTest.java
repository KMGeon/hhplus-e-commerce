package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.config.AbstractRestDocsTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends AbstractRestDocsTests {

    @Test
    @DisplayName("주문 생성 API - 성공")
    void createOrder_Success() throws Exception {
        // given
        String requestBody = """
                {
                    "userId": 1,
                    "couponId": null,
                    "products": [
                        {
                            "productId": 1,
                            "quantity": 2
                        },
                        {
                            "productId": 2,
                            "quantity": 1
                        }
                    ]
                }
                """;

        // when
        // then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(""))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.data").value(1))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER)
                                        .description("사용자 ID")
                                        .attributes(key("constraints").value("필수 값")),
                                fieldWithPath("couponId").type(JsonFieldType.NULL)
                                        .description("쿠폰 ID")
                                        .optional(),
                                fieldWithPath("products").type(JsonFieldType.ARRAY)
                                        .description("주문 상품 목록")
                                        .attributes(key("constraints").value("최소 1개 이상")),
                                fieldWithPath("products[].productId").type(JsonFieldType.NUMBER)
                                        .description("상품 ID")
                                        .attributes(key("constraints").value("필수 값")),
                                fieldWithPath("products[].quantity").type(JsonFieldType.NUMBER)
                                        .description("주문 수량")
                                        .attributes(key("constraints").value("1개 이상"))
                        ),
                        responseFields(COMMON_RESPONSE_FIELDS)
                ));
    }

    @Test
    @DisplayName("주문 생성 API - 실패 (상품 목록 없음)")
    void createOrder_Fail_NoProducts() throws Exception {
        // given
        String requestBody = """
                {
                    "userId": 1,
                    "couponId": null,
                    "products": []
                }
                """;

        // when
        // then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        responseFields(ERROR_RESPONSE_FIELDS)
                ));
    }

    @Test
    @DisplayName("주문 생성 API - 실패 (사용자 ID 없음)")
    void createOrder_Fail_NoUserId() throws Exception {
        // given
        String requestBody = """
                {
                    "couponId": null,
                    "products": [
                        {
                            "productId": 1,
                            "quantity": 2
                        }
                    ]
                }
                """;

        // when
        // then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        responseFields(ERROR_RESPONSE_FIELDS)
                ));
    }

    @Test
    @DisplayName("결제 API - 성공")
    void payment_Success() throws Exception {
        // given
        String requestBody = """
            {
                "userId": "user123",
                "orderId": 100,
                "couponId": "COUPON123"
            }
            """;

        // when
        // then
        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(""))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.data").value(1))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.STRING)
                                        .description("사용자 ID (필수 값)"),
                                fieldWithPath("orderId").type(JsonFieldType.NUMBER)
                                        .description("주문 ID (필수 값)"),
                                fieldWithPath("couponId").type(JsonFieldType.STRING)
                                        .description("쿠폰 ID (선택 사항)")
                                        .optional()
                        ),
                        responseFields(COMMON_RESPONSE_FIELDS)
                ));
    }

    @Test
    @DisplayName("결제 API - 실패 (사용자 ID 없음)")
    void payment_Fail_NoUserId() throws Exception {
        // given
        String requestBody = """
                {
                    "orderId": 100,
                    "couponId": "COUPON123"
                }
                """;

        // when
        // then
        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        responseFields(ERROR_RESPONSE_FIELDS)
                ));
    }

    @Test
    @DisplayName("결제 API - 실패 (주문 ID 없음)")
    void payment_Fail_NoOrderId() throws Exception {
        // given
        String requestBody = """
                {
                    "userId": "user123",
                    "couponId": "COUPON123"
                }
                """;

        // when
        // then
        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                        responseFields(ERROR_RESPONSE_FIELDS)
                ));
    }
}