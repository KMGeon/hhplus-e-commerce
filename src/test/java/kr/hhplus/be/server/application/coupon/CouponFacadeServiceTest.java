package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponFacadeServiceTest {

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponFacadeService couponFacadeService;

    private List<CouponInfo.CouponAvailable> couponAvailableList;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        couponAvailableList = Arrays.asList(
                new CouponInfo.CouponAvailable(100L, Arrays.asList(1L, 2L, 3L)),
                new CouponInfo.CouponAvailable(200L, Arrays.asList(4L, 5L))
        );
    }

    @Test
    void 배치_처리_성공() {
        // given
        when(couponService.processBatchInsert()).thenReturn(couponAvailableList);

        // when
        assertDoesNotThrow(() -> couponFacadeService.processBatchExecute());

        // then
        verify(couponService, times(1)).processBatchInsert();

        verify(couponService).decreaseCouponQuantity(100L, 3);
        verify(userCouponService).batchPublishUserCoupon(100L, Arrays.asList(1L, 2L, 3L));

        verify(couponService).decreaseCouponQuantity(200L, 2);
        verify(userCouponService).batchPublishUserCoupon(200L, Arrays.asList(4L, 5L));

        InOrder inOrder = inOrder(couponService, userCouponService);
        inOrder.verify(couponService).processBatchInsert();
        inOrder.verify(couponService).decreaseCouponQuantity(100L, 3);
        inOrder.verify(userCouponService).batchPublishUserCoupon(100L, Arrays.asList(1L, 2L, 3L));
        inOrder.verify(couponService).decreaseCouponQuantity(200L, 2);
        inOrder.verify(userCouponService).batchPublishUserCoupon(200L, Arrays.asList(4L, 5L));
    }

    @Test
    void 배치_처리_빈_유저_리스트_스킵() {
        // given
        List<CouponInfo.CouponAvailable> mixedList = Arrays.asList(
                new CouponInfo.CouponAvailable(100L, Arrays.asList(1L, 2L)),
                new CouponInfo.CouponAvailable(200L, Collections.emptyList()), // 빈 리스트
                new CouponInfo.CouponAvailable(300L, Arrays.asList(3L))
        );

        when(couponService.processBatchInsert()).thenReturn(mixedList);

        couponFacadeService.processBatchExecute();

        verify(couponService).decreaseCouponQuantity(100L, 2);
        verify(userCouponService).batchPublishUserCoupon(100L, Arrays.asList(1L, 2L));

        verify(couponService, never()).decreaseCouponQuantity(eq(200L), anyInt());
        verify(userCouponService, never()).batchPublishUserCoupon(eq(200L), anyList());

        verify(couponService).decreaseCouponQuantity(300L, 1);
        verify(userCouponService).batchPublishUserCoupon(300L, Arrays.asList(3L));
    }

    @Test
    void 쿠폰_재고_감소_실패() {
        // given
        when(couponService.processBatchInsert()).thenReturn(couponAvailableList);
        doThrow(new RuntimeException("쿠폰 재고가 부족합니다"))
                .when(couponService).decreaseCouponQuantity(100L, 3);

        // when
        // then
        assertThatThrownBy(() -> couponFacadeService.processBatchExecute())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("쿠폰 재고가 부족합니다");

        verify(userCouponService, never()).batchPublishUserCoupon(anyLong(), anyList());
    }

    @Test
    void 유저_쿠폰_발급_실패() {
        // given
        when(couponService.processBatchInsert()).thenReturn(couponAvailableList);
        doThrow(new RuntimeException("이미 발급한 쿠폰입니다"))
                .when(userCouponService).batchPublishUserCoupon(100L, Arrays.asList(1L, 2L, 3L));

        // when
        // then
        assertThatThrownBy(() -> couponFacadeService.processBatchExecute())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 발급한 쿠폰입니다");

        verify(couponService).decreaseCouponQuantity(100L, 3);
        verify(userCouponService).batchPublishUserCoupon(100L, Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void 배치_처리_결과_없음() {
        // given
        when(couponService.processBatchInsert()).thenReturn(Collections.emptyList());

        // when
        couponFacadeService.processBatchExecute();

        // then
        verify(couponService).processBatchInsert();
        verify(couponService, never()).decreaseCouponQuantity(anyLong(), anyInt());
        verify(userCouponService, never()).batchPublishUserCoupon(anyLong(), anyList());
    }

    @Test
    void 대량_배치_처리() {
        // given
        List<CouponInfo.CouponAvailable> largeBatch = Arrays.asList(
                new CouponInfo.CouponAvailable(100L, Arrays.asList(1L, 2L, 3L, 4L, 5L)),
                new CouponInfo.CouponAvailable(200L, Arrays.asList(6L, 7L, 8L)),
                new CouponInfo.CouponAvailable(300L, Collections.emptyList()),
                new CouponInfo.CouponAvailable(400L, Arrays.asList(9L, 10L))
        );

        when(couponService.processBatchInsert()).thenReturn(largeBatch);

        // when
        couponFacadeService.processBatchExecute();

        // then
        verify(couponService).processBatchInsert();

        verify(couponService).decreaseCouponQuantity(100L, 5);
        verify(userCouponService).batchPublishUserCoupon(100L, Arrays.asList(1L, 2L, 3L, 4L, 5L));

        verify(couponService).decreaseCouponQuantity(200L, 3);
        verify(userCouponService).batchPublishUserCoupon(200L, Arrays.asList(6L, 7L, 8L));

        verify(couponService, never()).decreaseCouponQuantity(eq(300L), anyInt());

        verify(couponService).decreaseCouponQuantity(400L, 2);
        verify(userCouponService).batchPublishUserCoupon(400L, Arrays.asList(9L, 10L));
    }

}