package kr.hhplus.be.server.interfaces.user;

import kr.hhplus.be.server.config.AbstractRestDocsTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends AbstractRestDocsTests {

    private static final String USER_ID = "1";

    @Nested
    @DisplayName("유저 포인트 조회")
    class UserPoint_GET {
        @Test
        @DisplayName("유저 포인트 조회 API")
        public void getUserPoint() throws Exception {
            // given
            // when
            // then
            mockMvc.perform(get("/api/v1/user/point")
                            .param("userId", USER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.amount").value(100))
                    .andDo(restDocs.document(
                            queryParameters(
                                    parameterWithName("userId").description("사용자 ID")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data").description("응답 데이터"),
                                    fieldWithPath("data.amount").description("포인트 금액")
                            )
                    ));

        }


        @Test
        @DisplayName("유저 포인트 조회 API - 파라미터 누락 시 오류")
        public void getUserPoint_파라미터누락_BadRequest() throws Exception {
            // given
            // when
            // then
            mockMvc.perform(get("/api/v1/user/point")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("A-002"))
                    .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                    .andDo(restDocs.document(
                            responseFields(ERROR_RESPONSE_FIELDS)
                    ));
        }
    }


    @Nested
    @DisplayName("유저 포인트 충전")
    class ChargeUserPointRequest {

        @Test
        public void chargeUserPoint_성공() throws Exception {
            // given
            String requestBody = """
                    {
                        "userId": "1",
                        "amount": 5000
                    }
                    """;

            // when
            // then
            mockMvc.perform(post("/api/v1/user/point")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(1))
                    .andDo(restDocs.document(
                            requestBody(),
                            responseFields(COMMON_RESPONSE_FIELDS)
                    ));
        }

        @Test
        @DisplayName("유저 포인트 충전 API - 유저 ID 누락 시 오류")
        public void chargeUserPoint_유저없음() throws Exception {
            // given
            String requestBody = """
                    {
                        "amount": 5000
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/user/point")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("A-002"))
                    .andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."))
                    .andDo(restDocs.document(
                            responseFields(ERROR_RESPONSE_FIELDS)
                    ));
        }

        @Test
        @DisplayName("유저 포인트 충전 API - 음수 금액 테스트")
        public void chargeUserPoint_음수금액() throws Exception {
            // given
            String requestBody = """
                    {
                        "userId": "1234",
                        "amount": -1000
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/user/point")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("A-002"))
                    .andExpect(jsonPath("$.message").value("충전 금액은 0보다 커야 합니다."))
                    .andDo(restDocs.document(
                            responseFields(ERROR_RESPONSE_FIELDS)
                    ));
        }


        @Test
        @DisplayName("유저 포인트 충전 API - 0원 충전 실패")
        public void chargeUserPoint_0원충전() throws Exception {
            // given
            String requestBody = """
                    {
                        "userId": "1234",
                        "amount": 0
                    }
                    """;

            // when
            // then
            mockMvc.perform(post("/api/v1/user/point")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("A-002"))
                    .andExpect(jsonPath("$.message").value("충전 금액은 0보다 커야 합니다."))
                    .andDo(restDocs.document(
                            responseFields(ERROR_RESPONSE_FIELDS)
                    ));
        }
    }

}