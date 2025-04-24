package kr.hhplus.be.server.interfaces.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserInfo;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("유저 포인트 조회 테스트")
    void getUserPointTest() throws Exception {
        // given
        Long userId = 1L;
        UserInfo.User mockUser = new UserInfo.User(userId, 1000L);
        given(userService.getUser(anyLong())).willReturn(mockUser);

        // when & then
        mockMvc.perform(get("/api/v1/user/point")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 포인트 충전 테스트")
    void chargeUserPointTest() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 500L;
        UserRequest.ChargeUserPointRequest request = new UserRequest.ChargeUserPointRequest(userId, amount);
        UserInfo.User chargedUser = new UserInfo.User(userId, 1500L);
        
        given(userService.charge(any(UserCommand.PointCharge.class))).willReturn(chargedUser);

        // when & then
        mockMvc.perform(post("/api/v1/user/point")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 포인트 충전 요청 유효성 검사 실패 테스트")
    void chargeUserPointValidationFailTest() throws Exception {
        // given
        UserRequest.ChargeUserPointRequest invalidRequest = new UserRequest.ChargeUserPointRequest(null, -100L);

        // when & then
        mockMvc.perform(post("/api/v1/user/point")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}