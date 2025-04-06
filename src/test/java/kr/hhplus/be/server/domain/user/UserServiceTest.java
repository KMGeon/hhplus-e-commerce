package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.application.user.UserChargeCommand;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.domain.user.UserEntity.initializeUserEntity;
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

    @Nested
    class 충전 {

        @Test
        public void 포인트_충전이_성공적으로_수행된다() throws Exception {
            // given
            UserEntity user1 = initializeUserEntity(1L, 1000L);
            UserEntity updatedUser = initializeUserEntity(1L, 2000L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
            when(userRepository.update(any(UserEntity.class))).thenReturn(updatedUser);

            UserChargeCommand command = new UserChargeCommand(1L, 1000L);

            // when
            UserEntity charge = userService.charge(command);

            // then
            assertNotNull(charge);
            assertEquals(2000L, charge.getPoint().getAmount());
        }

    }
}