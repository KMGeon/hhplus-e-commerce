package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.order.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserEntityTest {

    @Test
    public void 신규유저_생성() throws Exception{
        // given

        // when
        UserEntity newUser = UserEntity.createNewUser();

        // then
        assertEquals(newUser.getPoint(), 0L,"신규 유저의 포인트는 0원 입니다.");
    }

    @Test
    public void 유저_포인트_충전() throws Exception{
        // given
        final long chargeAmount = 1000L;
        UserEntity newUser = UserEntity.createNewUser();
        // when
        UserEntity expectUser = newUser.chargePoint(chargeAmount);

        // then
        assertEquals(expectUser.getPoint(),chargeAmount,"1000원 충전 후 포인트는 1000원 입니다.");
    }


    @Test
    public void 유저_포인트_사용() throws Exception{
        // given
        final long chargeAmount = 1000L;
        final long useAmount = 500L;
        final long expectAmount = chargeAmount - useAmount;
        
        UserEntity existUser = UserEntity.createNewUser().chargePoint(chargeAmount);

        // when
        existUser.usePoint(useAmount);

        // then
        assertEquals(existUser.getPoint(),expectAmount,"1000원이 있는 유저에서 500원을 사용하면 500원이 남습니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -200L})
    void 유저_포인트_사용_유효성_실패(long invalidAmount) {
        // given
        final long chargeAmount = 1000L;
        UserEntity existUser = UserEntity.createNewUser().chargePoint(chargeAmount);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            existUser.usePoint(invalidAmount);
        }, "0 또는 음수 금액으로 포인트를 사용하려 할 때 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("새 사용자 생성 테스트")
    void createNewUser() {
        // when
        UserEntity user = UserEntity.createNewUser();

        // then
        assertNotNull(user);
        assertEquals(0, user.getPoint());
    }

    @Test
    @DisplayName("포인트 충전 테스트")
    void chargePoint() {
        // given
        UserEntity user = UserEntity.createNewUser();
        long amount = 10000;

        // when
        UserEntity result = user.chargePoint(amount);

        // then
        assertEquals(amount, user.getPoint());
        assertSame(user, result); // 메서드 체이닝을 위해 자기 자신을 반환
    }

    @Test
    @DisplayName("포인트 충전 - 음수 금액 예외 테스트")
    void chargePoint_negativeAmount() {
        // given
        UserEntity user = UserEntity.createNewUser();
        long negativeAmount = -1000;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> user.chargePoint(negativeAmount));
    }

    @Test
    @DisplayName("포인트 충전 - 0원 금액 예외 테스트")
    void chargePoint_zeroAmount() {
        // given
        UserEntity user = UserEntity.createNewUser();
        long zeroAmount = 0;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> user.chargePoint(zeroAmount));
    }

    @Test
    @DisplayName("포인트 사용 테스트")
    void usePoint() {
        // given
        UserEntity user = UserEntity.createNewUser();
        long chargeAmount = 10000;
        long useAmount = 3000;
        user.chargePoint(chargeAmount);

        // when
        user.usePoint(useAmount);

        // then
        assertEquals(chargeAmount - useAmount, user.getPoint());
    }

    @Test
    @DisplayName("포인트 사용 - 음수 금액 예외 테스트")
    void usePoint_negativeAmount() {
        // given
        UserEntity user = UserEntity.createNewUser();
        user.chargePoint(10000);
        long negativeAmount = -1000;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> user.usePoint(negativeAmount));
    }

    @Test
    @DisplayName("포인트 사용 - 0원 금액 예외 테스트")
    void usePoint_zeroAmount() {
        // given
        UserEntity user = UserEntity.createNewUser();
        user.chargePoint(10000);
        long zeroAmount = 0;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> user.usePoint(zeroAmount));
    }

    @Test
    @DisplayName("결제 성공 테스트")
    void pay_success() {
        // given
        UserEntity user = UserEntity.createNewUser();
        user.chargePoint(10000);

        OrderEntity mockOrder = Mockito.mock(OrderEntity.class);
        BigDecimal orderAmount = new BigDecimal("5000");

        when(mockOrder.getFinalAmount()).thenReturn(orderAmount);

        // when
        user.pay(mockOrder);

        // then
        assertEquals(5000, user.getPoint()); // 10000 - 5000 = 5000

        // 메소드 호출 검증
        verify(mockOrder).validatePaymentAvailable();
        verify(mockOrder).applyDiscount();
        verify(mockOrder).complete();
        verify(mockOrder, times(2)).getFinalAmount(); // validatePointAvailable 및 usePoint에서 각각 한 번씩
    }

    @Test
    @DisplayName("결제 실패 - 포인트 부족 테스트")
    void pay_insufficientPoint() {
        // given
        UserEntity user = UserEntity.createNewUser();
        user.chargePoint(1000); // 1000 포인트만 충전

        OrderEntity mockOrder = Mockito.mock(OrderEntity.class);
        BigDecimal orderAmount = new BigDecimal("5000"); // 주문 금액은 5000

        when(mockOrder.getFinalAmount()).thenReturn(orderAmount);

        // when & then
        assertThrows(IllegalStateException.class, () -> user.pay(mockOrder));

        // 메소드 호출 검증
        verify(mockOrder).validatePaymentAvailable();
        verify(mockOrder).getFinalAmount(); // validatePointAvailable에서 한 번
        verify(mockOrder).applyDiscount();
        verify(mockOrder, never()).complete(); // 예외 발생으로 호출되지 않아야 함
    }

    @Test
    @DisplayName("결제 검증 실패 테스트")
    void pay_invalidPayment() {
        // given
        UserEntity user = UserEntity.createNewUser();
        user.chargePoint(10000);

        OrderEntity mockOrder = Mockito.mock(OrderEntity.class);

        // 결제 검증에서 예외 발생
        doThrow(new IllegalStateException("결제 불가능한 주문입니다")).when(mockOrder).validatePaymentAvailable();

        // when & then
        assertThrows(IllegalStateException.class, () -> user.pay(mockOrder));

        // 메소드 호출 검증
        verify(mockOrder).validatePaymentAvailable();
        verify(mockOrder, never()).getFinalAmount(); // 예외 발생으로 호출되지 않아야 함
        verify(mockOrder, never()).applyDiscount(); // 예외 발생으로 호출되지 않아야 함
        verify(mockOrder, never()).complete(); // 예외 발생으로 호출되지 않아야 함
    }

}