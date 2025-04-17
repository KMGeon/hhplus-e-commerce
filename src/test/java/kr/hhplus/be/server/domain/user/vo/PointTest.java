package kr.hhplus.be.server.domain.user.vo;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @Nested
    @DisplayName("포인트 생성 테스트")
    class CreatePointTest {

        @Test
        @DisplayName("zero 메소드로 0 포인트 생성")
        void createZeroPoint() {
            // when
            Point point = Point.zero();

            // then
            assertThat(point.getAmount()).isEqualTo(0L);
        }

        @ParameterizedTest
        @ValueSource(longs = {0, 1, 100, 999999})
        @DisplayName("정상적인 포인트 값으로 생성")
        void createValidPoint(long amount) {
            // when
            Point point = Point.of(amount);

            // then
            assertThat(point.getAmount()).isEqualTo(amount);
        }

        @Test
        @DisplayName("음수 값으로 포인트 생성 시 예외 발생")
        void createPointWithNegativeAmount() {
            // when & then
            assertThatThrownBy(() -> Point.of(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("포인트는 음수가 될 수 없습니다");
        }

        @Test
        @DisplayName("최대 제한 포인트를 초과하는 값으로 생성 시 정상 생성")
        void createPointExceedingLimit() {
            // 1,000,000 포인트는 제한에 걸리지 않음 (초과해야 예외 발생)
            Point point = Point.of(1_000_000L);
            assertThat(point.getAmount()).isEqualTo(1_000_000L);
        }
    }

    @Nested
    @DisplayName("포인트 추가 테스트")
    class AddPointTest {

        @Test
        @DisplayName("0 포인트 추가 시 동일한 객체 반환")
        void addZeroPoint() {
            // given
            Point point = Point.of(100L);

            // when
            Point result = point.add(0L);

            // then
            assertThat(result.getAmount()).isEqualTo(100L);
        }

        @Test
        @DisplayName("양수 포인트 정상 추가")
        void addPositivePoint() {
            // given
            Point point = Point.of(100L);

            // when
            Point result = point.add(50L);

            // then
            assertThat(result.getAmount()).isEqualTo(150L);
        }

        @Test
        @DisplayName("음수 포인트 추가 시 예외 발생")
        void addNegativePoint() {
            // given
            Point point = Point.of(100L);

            // when & then
            assertThatThrownBy(() -> point.add(-50L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추가할 포인트는 양수여야 합니다");
        }

        @Test
        @DisplayName("포인트 추가 후 최대 제한 초과 시 예외 발생")
        void addPointExceedingLimit() {
            // given
            Point point = Point.of(999_000L);

            // when & then
            assertThatThrownBy(() -> point.add(2_000L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("포인트는 최대 1000000을 초과할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("포인트 차감 테스트")
    class DecreasePointTest {

        @Test
        @DisplayName("0 포인트 차감 시 동일한 객체 반환")
        void decreaseZeroPoint() {
            // given
            Point point = Point.of(100L);

            // when
            Point result = point.decreasePoint(0L);

            // then
            assertThat(result.getAmount()).isEqualTo(100L);
        }

        @Test
        @DisplayName("양수 포인트 정상 차감")
        void decreasePositivePoint() {
            // given
            Point point = Point.of(100L);

            // when
            Point result = point.decreasePoint(50L);

            // then
            assertThat(result.getAmount()).isEqualTo(50L);
        }

        @Test
        @DisplayName("음수 포인트 차감 시 예외 발생")
        void decreaseNegativePoint() {
            // given
            Point point = Point.of(100L);

            // when & then
            assertThatThrownBy(() -> point.decreasePoint(-50L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("차감할 포인트는 양수여야 합니다");
        }

        @Test
        @DisplayName("보유 포인트보다 많은 포인트 차감 시 예외 발생")
        void decreasePointMoreThanBalance() {
            // given
            Point point = Point.of(100L);

            // when & then
            assertThatThrownBy(() -> point.decreasePoint(150L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("차감할 포인트가 보유 포인트보다 많습니다");
        }

        @Test
        @DisplayName("보유 포인트와 동일한 포인트 차감 시 0 포인트 반환")
        void decreaseAllPoints() {
            // given
            Point point = Point.of(100L);

            // when
            Point result = point.decreasePoint(100L);

            // then
            assertThat(result.getAmount()).isEqualTo(0L);
        }
    }
}