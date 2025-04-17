package kr.hhplus.be.server.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private static final Long EXIST_USER = 1L;
    private static final Long NO_SIGNUP_USER = 100L;

    @Nested
    class 충전 {

        @ParameterizedTest
        @ValueSource(longs = {1000L, 2000L, 3000L})
        @DisplayName("포인트 충전 테스트")
        public void 포인트_충전이_성공적으로_수행된다(long chargeAmount) throws Exception {
            // given
            UserEntity initialUser = UserEntity.createNewUser();
            UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(1L, chargeAmount);

            when(userRepository.findById(any())).thenReturn(Optional.of(initialUser));

            // when
            UserInfo.UserChargeInfo rtn = userService.charge(pointCharge);

            // then
            assertEquals(rtn.amount(),chargeAmount,"기본 유저에서 포인트를 충전하면 충전된 포인트가 반환되어야 한다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1000L, -2000L})
        public void 음수_및_Zero_포인트_충전(long chargeAmount) throws Exception {
            // given
            UserEntity initialUser = UserEntity.createNewUser();
            UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(1L, chargeAmount);
            when(userRepository.findById(any())).thenReturn(Optional.of(initialUser));

            // when
            // then
            Assertions.assertThatThrownBy(() -> userService.charge(pointCharge))
                            .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("충전 금액은 양수여야 합니다");
        }
    }
}