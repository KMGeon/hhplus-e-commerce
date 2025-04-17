package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

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

}