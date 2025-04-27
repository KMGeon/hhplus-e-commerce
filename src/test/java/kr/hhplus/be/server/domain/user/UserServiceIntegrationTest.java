package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.config.ApplicationContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UserServiceIntegrationTest extends ApplicationContext {

    private Long userId = 0L;

    @Nested
    class 포인트_충전 {

        @BeforeEach
        public void setUp() {
            UserEntity newUser = UserEntity.createNewUser();
            userId = userJpaRepository.save(newUser).getId();
        }

        @AfterEach
        public void clear() {
            userJpaRepository.deleteAll();
        }

        @Test
        @DisplayName("""
                1. 사용자 생성
                2. 포인트 충전 (양수)
                """)
        public void 포인트_충전() throws Exception {
            // given
            UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(userId, 1000L);

            // when
            UserInfo.User charge = userService.charge(pointCharge);
            UserEntity getUser = userJpaRepository.findById(userId).orElseThrow();

            // then
            assertEquals(charge.amount(),getUser.getPoint());
        }

        @Test
        @DisplayName("""
                1. 사용자 생성
                2. 포인트 충전 0원
                3. 예외 발생
                """)
        public void 포인트_충전_실패_0원() throws Exception{
            // given
            UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(userId, 0L);

            // when
            // then
            Assertions.assertThatThrownBy(() -> userService.charge(pointCharge))
                            .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("충전 금액은 양수여야 합니다");
        }

        @Test
        @DisplayName("""
                1. 사용자 생성
                2. 포인트 충전 0원
                3. 예외 발생
                """)
        public void 포인트_충전_실패_음수() throws Exception{
            // given
            UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(userId, -1000L);

            // when
            // then
            Assertions.assertThatThrownBy(() -> userService.charge(pointCharge))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("충전 금액은 양수여야 합니다");
        }
    }
}