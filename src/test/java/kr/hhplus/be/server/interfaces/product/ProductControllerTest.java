//package kr.hhplus.be.server.interfaces.product;
//
//import kr.hhplus.be.server.config.AbstractRestDocsTests;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.payload.JsonFieldType;
//
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
//import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
//import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class ProductControllerTest extends AbstractRestDocsTests {
//    @Test
//    @DisplayName("카테고리별 상품 목록 조회 - 카테고리 있음")
//    public void getProducts_WithCategory() throws Exception {
//        // given
//        String category = "G";
//
//        // when
//        // then
//        mockMvc.perform(get("/api/v1/product")
//                        .param("category", category)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.length()").value(1))
//                .andExpect(jsonPath("$.data[0].productName").value("카테고리_G_상품"))
//                .andDo(restDocs.document(
//                        queryParameters(
//                                parameterWithName("category").description("카테고리")
//                        ),
//                        responseFields(
//                                fieldWithPath("code").description("응답 코드"),
//                                fieldWithPath("message").description("응답 메시지"),
//                                fieldWithPath("data").description("응답 데이터"),
//                                fieldWithPath("data[]").description("상품 목록"),
//                                fieldWithPath("data[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
//                                fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("상품명"),
//                                fieldWithPath("data[].productPrice").type(JsonFieldType.NUMBER).description("상품 가격"),
//                                fieldWithPath("data[].stockQuantity").type(JsonFieldType.NUMBER).description("재고 수량")
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("카테고리별 상품 목록 조회 - 카테고리 없음")
//    public void getProducts_WithoutCategory() throws Exception {
//        // given
//        // when
//        // then
//        mockMvc.perform(get("/api/v1/product")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.length()").value(3))
//                .andExpect(jsonPath("$.data[0].productName").value("상품1"))
//                .andExpect(jsonPath("$.data[1].productName").value("상품2"))
//                .andExpect(jsonPath("$.data[2].productName").value("상품3"))
//                .andDo(restDocs.document(
//                        queryParameters(
//                                parameterWithName("category").description("카테고리").optional()
//                        ),
//                        responseFields(
//                                fieldWithPath("code").description("응답 코드"),
//                                fieldWithPath("message").description("응답 메시지"),
//                                fieldWithPath("data").description("응답 데이터"),
//                                fieldWithPath("data[]").description("상품 목록"),
//                                fieldWithPath("data[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
//                                fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("상품명"),
//                                fieldWithPath("data[].productPrice").type(JsonFieldType.NUMBER).description("상품 가격"),
//                                fieldWithPath("data[].stockQuantity").type(JsonFieldType.NUMBER).description("재고 수량")
//                        )
//
//                ));
//    }
//
//    @Test
//    @DisplayName("인기 상품 목록 조회")
//    public void getHotProducts_Success() throws Exception {
//        // when
//        // then
//        mockMvc.perform(get("/api/v1/hot-product")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.length()").value(3))
//                .andExpect(jsonPath("$.data[0].productName").value("상품1"))
//                .andExpect(jsonPath("$.data[1].productName").value("상품2"))
//                .andExpect(jsonPath("$.data[2].productName").value("상품3"))
//                .andDo(restDocs.document(
//                        responseFields(
//                                fieldWithPath("code").description("응답 코드"),
//                                fieldWithPath("message").description("응답 메시지"),
//                                fieldWithPath("data").description("응답 데이터"),
//                                fieldWithPath("data[]").description("인기 상품 목록"),
//                                fieldWithPath("data[].productId").description("상품 ID"),
//                                fieldWithPath("data[].productName").description("상품명"),
//                                fieldWithPath("data[].productPrice").description("상품 가격"),
//                                fieldWithPath("data[].stockQuantity").description("재고 수량")
//                        )
//                ));
//    }
//}