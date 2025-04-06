package kr.hhplus.be.server.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({RestDocsConfiguration.class, ObjectMapper.class})
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
public abstract class AbstractRestDocsTests {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected MockMvc mockMvc;

    protected static final FieldDescriptor[] COMMON_RESPONSE_FIELDS = new FieldDescriptor[] {
            fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
            fieldWithPath("data").type(JsonFieldType.NUMBER).description("결과")
    };

    protected static final FieldDescriptor[] ERROR_RESPONSE_FIELDS = new FieldDescriptor[] {
            fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
            fieldWithPath("data").optional().type(JsonFieldType.NULL).description("결제 결과 (없을 수 있음)")
    };

    @BeforeEach
    void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}