package kr.hhplus.be.server.interfaces.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderFacadeService orderFacadeService;

    @Captor
    private ArgumentCaptor<OrderCriteria.Order> orderCriteriaCaptor;

    @Test
    @DisplayName("주문 생성 성공 테스트")
    void createOrder_Success() throws Exception {
        // given
        OrderRequestDTO.CreateOrderRequest request = new OrderRequestDTO.CreateOrderRequest(
                1L,
                List.of(
                        new OrderRequestDTO.OrderProductRequest("SKU001", 2),
                        new OrderRequestDTO.OrderProductRequest("SKU002", 3)
                )
        );

        Long orderId = 123L;
        when(orderFacadeService.createOrder(any(OrderCriteria.Order.class))).thenReturn(orderId);

        // when & then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(""))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.data").value(orderId));

        // verify
        verify(orderFacadeService).createOrder(orderCriteriaCaptor.capture());
        OrderCriteria.Order capturedCriteria = orderCriteriaCaptor.getValue();
        
        assertThat(capturedCriteria.userId()).isEqualTo(1L);
        assertThat(capturedCriteria.products()).hasSize(2);
        assertThat(capturedCriteria.products().get(0).skuId()).isEqualTo("SKU001");
        assertThat(capturedCriteria.products().get(0).ea()).isEqualTo(2);
        assertThat(capturedCriteria.products().get(1).skuId()).isEqualTo("SKU002");
        assertThat(capturedCriteria.products().get(1).ea()).isEqualTo(3);
    }

    @Test
    @DisplayName("주문 생성 실패 - 사용자 ID가 null인 경우")
    void createOrder_Fail_NullUserId() throws Exception {
        // given
        OrderRequestDTO.CreateOrderRequest request = new OrderRequestDTO.CreateOrderRequest(
                null,
                List.of(
                        new OrderRequestDTO.OrderProductRequest("SKU001", 2)
                )
        );

        // when & then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품 목록이 비어있는 경우")
    void createOrder_Fail_EmptyProducts() throws Exception {
        // given
        OrderRequestDTO.CreateOrderRequest request = new OrderRequestDTO.CreateOrderRequest(
                1L,
                List.of()
        );

        // when & then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품 수량이 0인 경우")
    void createOrder_Fail_ZeroQuantity() throws Exception {
        // given
        OrderRequestDTO.CreateOrderRequest request = new OrderRequestDTO.CreateOrderRequest(
                1L,
                List.of(
                        new OrderRequestDTO.OrderProductRequest("SKU001", 0)
                )
        );

        // when & then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품 ID가 null인 경우")
    void createOrder_Fail_NullProductId() throws Exception {
        // given
        OrderRequestDTO.CreateOrderRequest request = new OrderRequestDTO.CreateOrderRequest(
                1L,
                List.of(
                        new OrderRequestDTO.OrderProductRequest(null, 2)
                )
        );

        // when & then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}