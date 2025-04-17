package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.order.OrderCoreRepository;
import kr.hhplus.be.server.domain.order.OrderEntity;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

    @ParameterizedTest
    @ValueSource(longs = {1000L, 2000L, 3000L})
    @DisplayName("포인트 충전 테스트")
    public void 포인트_충전이_성공적으로_수행된다(long chargeAmount) throws Exception {
        // given
        UserEntity initialUser = UserEntity.createNewUser();
        UserCommand.PointCharge pointCharge = new UserCommand.PointCharge(EXIST_USER, chargeAmount);

        when(userRepository.findById(EXIST_USER)).thenReturn(Optional.of(initialUser));

        // when
        UserInfo.UserChargeInfo rtn = userService.charge(pointCharge);

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
        when(userRepository.findById(EXIST_USER)).thenReturn(Optional.of(initialUser));

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
        when(userRepository.findById(NO_SIGNUP_USER)).thenReturn(Optional.empty());

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.charge(pointCharge))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("해당 유저가 존재하지 않습니다");
    }

    @Test
    @DisplayName("사용자 포인트 조회 성공")
    public void 유저_포인트_조회() throws Exception {
        // given
        final long point = 1000L;
        UserEntity initialUser = UserEntity.createNewUser();
        UserEntity userEntity = initialUser.chargePoint(point);
        when(userRepository.findById(EXIST_USER)).thenReturn(Optional.of(userEntity));

        // when
        long userPoint = userService.getUserPoint(EXIST_USER);

        // then
        assertEquals(point, userPoint);
    }

    @Test
    public void 존재하지_않는_사용자_포인트_조회() {
        // given
        when(userRepository.findById(NO_SIGNUP_USER)).thenReturn(Optional.empty());

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getUserPoint(NO_SIGNUP_USER))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("해당 유저가 존재하지 않습니다");
    }

    @Test
    public void 존재하지_않는_사용자_ID_조회() {
        // given
        when(userRepository.findById(NO_SIGNUP_USER)).thenReturn(Optional.empty());

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getUserId(NO_SIGNUP_USER))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("해당 유저가 존재하지 않습니다");
    }

    @Test
    public void 결제_처리_주문_없음() {
        // given
        when(orderCoreRepository.findById(TEST_ORDER_ID)).thenReturn(Optional.empty());

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.payProcess(EXIST_USER, TEST_ORDER_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("해당 주문이 존재하지 않습니다");

        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    public void 결제_처리_사용자_없음() {
        // given
        OrderEntity order = mock(OrderEntity.class);
        when(orderCoreRepository.findById(TEST_ORDER_ID)).thenReturn(Optional.of(order));
        when(userRepository.findById(NO_SIGNUP_USER)).thenReturn(Optional.empty());

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.payProcess(NO_SIGNUP_USER, TEST_ORDER_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("해당 유저가 존재하지 않습니다");

        verify(orderCoreRepository).findById(TEST_ORDER_ID);
    }

    @Test
    public void 유저_쿠폰_검증() throws Exception {
        // given
        UserEntity initialUser = spy(UserEntity.createNewUser());
        when(initialUser.getId()).thenReturn(EXIST_USER); // ID 값을 명시적으로 설정
        when(userRepository.findById(EXIST_USER)).thenReturn(Optional.of(initialUser));
        when(userCouponRepository.existsCoupon(EXIST_USER, TEST_COUPON_ID)).thenReturn(false);

        // when
        long result = userService.validateUserForCoupon(EXIST_USER, TEST_COUPON_ID);

        // then
        assertEquals(EXIST_USER, result);
        verify(userCouponRepository).existsCoupon(EXIST_USER, TEST_COUPON_ID);
    }

    @Test
    public void 존재하지_않는_사용자_쿠폰_검증() {
        // given
        when(userRepository.findById(NO_SIGNUP_USER)).thenReturn(Optional.empty());

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.validateUserForCoupon(NO_SIGNUP_USER, TEST_COUPON_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("해당 유저가 존재하지 않습니다");

        // 유저가 존재하지 않으므로 쿠폰 검증은 호출되지 않아야 함
        verify(userCouponRepository, never()).existsCoupon(anyLong(), anyLong());
    }

    @Test
    public void 이미_쿠폰_보유_사용자_검증() {
        // given
        UserEntity mockUser = mock(UserEntity.class);
        when(mockUser.getId()).thenReturn(EXIST_USER);

        when(userRepository.findById(EXIST_USER)).thenReturn(Optional.of(mockUser));
        when(userCouponRepository.existsCoupon(EXIST_USER, TEST_COUPON_ID)).thenReturn(true);

        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.validateUserForCoupon(EXIST_USER, TEST_COUPON_ID))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("사용자가 이미 해당 쿠폰을 보유하고 있습니다");

        // 쿠폰 검증이 호출되었는지 확인
        verify(userCouponRepository).existsCoupon(EXIST_USER, TEST_COUPON_ID);
    }

}