package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.command.UserChargeCommand;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
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

        @Test
        public void 포인트_충전이_성공적으로_수행된다() {
            // given
            long initialAmount = 0L;
            long chargeAmount = 1000L;
            long expectedAmount = initialAmount + chargeAmount;

            UserEntity initialUser = UserEntity.createNewUser();
            UserChargeCommand command = new UserChargeCommand(EXIST_USER, chargeAmount);

            when(userRepository.findById(any())).thenReturn(Optional.of(initialUser));
            when(userRepository.update(any(UserEntity.class))).thenReturn(initialUser);

            // when
            UserEntity user = userService.charge(command);

            // then
            assertNotNull(user);
            long point = user.getPoint();
            assertEquals(point, expectedAmount);
        }

        @Test
        public void 없는_사용자_포인트_충전() throws Exception {
            // given
            UserChargeCommand command = new UserChargeCommand(NO_SIGNUP_USER, 1000L);

            // when
            // then
            Assertions.assertThatThrownBy(() -> userService.charge(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("해당 유저가 존재하지 않습니다.");
        }
    }
}

// 크리테리아 facade
// command