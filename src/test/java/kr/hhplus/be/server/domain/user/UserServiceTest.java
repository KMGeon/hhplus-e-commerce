package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.order.OrderCoreRepository;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private OrderCoreRepository orderCoreRepository;

    private static final Long EXIST_USER = 1L;
    private static final Long NO_SIGNUP_USER = 100L;
    private static final Long TEST_COUPON_ID = 10L;
    private static final Long TEST_ORDER_ID = 1000L;


    @Test
    @DisplayName("사용자 포인트 조회 성공")
    public void 유저_포인트_조회() throws Exception {
        // given
        final long point = 1000L;
        UserEntity initialUser = UserEntity.createNewUser();
        UserEntity userEntity = initialUser.chargePoint(point);
        when(userRepository.findById(EXIST_USER)).thenReturn(userEntity);

        // when
        UserInfo.User getUser = userService.getUser(EXIST_USER);

        // then
        assertEquals(point, getUser.amount());
    }

    @Test
    public void 존재하지_않는_사용자_포인트_조회() {
        // given
        when(userRepository.findById(NO_SIGNUP_USER))
                .thenThrow(new IllegalArgumentException(String.format("회원을 찾을 수 없습니다. id: %s", NO_SIGNUP_USER)));

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getUser(NO_SIGNUP_USER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }


    @ParameterizedTest
    @ValueSource(longs = {1000L, 2000L, 3000L})
    @DisplayName("포인트 충전 테스트")
    public void 포인트_충전이_성공적으로_수행된다(long chargeAmount) throws Exception {
        // given
        UserEntity initialUser = UserEntity.createNewUser();
        UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(EXIST_USER, chargeAmount);

        when(userRepository.findById(EXIST_USER)).thenReturn(initialUser);

        // when
        UserInfo.User rtn = userService.charge(pointCharge);

        // then
        assertEquals(rtn.amount(), chargeAmount, "기본 유저에서 포인트를 충전하면 충전된 포인트가 반환되어야 한다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1000L, -2000L})
    @DisplayName("음수 및 0 포인트 충전 시 예외 발생")
    public void 음수_및_Zero_포인트_충전(long chargeAmount) throws Exception {
        // given
        UserEntity initialUser = UserEntity.createNewUser();
        UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(EXIST_USER, chargeAmount);
        when(userRepository.findById(EXIST_USER)).thenReturn(initialUser);

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.charge(pointCharge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 양수여야 합니다");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 충전 시 예외 발생")
    public void 존재하지_않는_사용자_충전() {
        // given
        UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(NO_SIGNUP_USER, 1000L);
        // given
        when(userRepository.findById(NO_SIGNUP_USER))
                .thenThrow(new IllegalArgumentException(String.format("회원을 찾을 수 없습니다. id: %s", NO_SIGNUP_USER)));

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.charge(pointCharge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("""
            findById로 유저는 테스트 완료.
            포인트 사용의 로직은 Domain에서 선 테스트 완료
            성공 케이스만 작성
            """)
    public void 포인트_사용() throws Exception {
        // given
        final long point = 5000L;
        UserEntity initialUser = UserEntity.createNewUser();
        UserEntity userEntity = initialUser.chargePoint(point);
        when(userRepository.findByIdOptimisticLock(EXIST_USER)).thenReturn(userEntity);

        // when
        userService.usePoint(EXIST_USER, BigDecimal.valueOf(1000L));

        // then
        assertEquals(userEntity.getPoint(), 4000L, "5000원 충전 - 1000원 사용 = 4000원");
    }
}