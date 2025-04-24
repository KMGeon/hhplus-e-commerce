package kr.hhplus.be.server.interfaces.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.application.payment.PaymentFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentFacadeService paymentFacadeService;

    @Test
    @DisplayName("결제 요청이 성공적으로 처리되어야 한다")
    void processPaymentShouldSucceed() throws Exception {
        // given
        PaymentRequest.PayRequest paymentRequest = new PaymentRequest.PayRequest(1L, 2L, 3L);
        
        // when & then
        doNothing().when(paymentFacadeService).payment(any(PaymentCriteria.Pay.class));

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());

        verify(paymentFacadeService).payment(any(PaymentCriteria.Pay.class));
    }

    @Test
    @DisplayName("필수 파라미터가 누락된 경우 예외가 발생해야 한다")
    void processPaymentShouldFailWhenRequiredParameterIsMissing() throws Exception {
        // given
        PaymentRequest.PayRequest invalidRequest = new PaymentRequest.PayRequest(null, 2L, 3L);
        
        // when & then
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("쿠폰 ID 없이 결제 요청이 성공적으로 처리되어야 한다")
    void processPaymentWithoutCouponShouldSucceed() throws Exception {
        // given
        PaymentRequest.PayRequest paymentRequest = new PaymentRequest.PayRequest(1L, 2L, null);
        
        // when & then
        doNothing().when(paymentFacadeService).payment(any(PaymentCriteria.Pay.class));

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());

        verify(paymentFacadeService).payment(any(PaymentCriteria.Pay.class));
    }
} 