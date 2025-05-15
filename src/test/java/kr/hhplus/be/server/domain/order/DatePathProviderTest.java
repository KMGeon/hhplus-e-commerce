package kr.hhplus.be.server.domain.order;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DatePathProviderTest {

    @Test
    public void 배열사용_날짜경로변환_테스트_수정() throws Exception {
        // given
        int initNum = 1000;
        LocalDateTime[] dates = new LocalDateTime[initNum];
        String[] paths = new String[initNum];
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        // when
        for (int i = 0; i < initNum; i++) {
            dates[i] = startDate.plusDays(i);
            paths[i] = DatePathProvider.toPath(dates[i]);
        }

        // then
        for (int i = 0; i < initNum; i++) {
            System.out.printf("dates[%d] = %s -> paths[%d] = %s%n",
                    i, dates[i], i, paths[i]);
        }
    }

    @Test
    @DisplayName("날짜를 경로로 변환 테스트 - 기준일")
    void toPath_withBaseDate_returnsCorrectPath() {
        // given
        LocalDateTime baseDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        // when
        String path = DatePathProvider.toPath(baseDateTime);

        // then
        assertEquals("00000", path);
    }

    @Test
    @DisplayName("날짜를 경로로 변환 테스트 - 기준일로부터 1일 후")
    void toPath_withOneDayAfterBase_returnsCorrectPath() {
        // given
        LocalDateTime oneDayAfter = LocalDateTime.of(2025, 1, 2, 0, 0, 0);

        // when
        String path = DatePathProvider.toPath(oneDayAfter);

        // then
        assertEquals("00001", path);
    }

    @Test
    @DisplayName("날짜를 경로로 변환 테스트 - 기준일로부터 36일 후 (36진수에서 10)")
    void toPath_with36DaysAfterBase_returnsCorrectPath() {
        // given
        LocalDateTime daysWith36 = LocalDateTime.of(2025, 1, 1, 0, 0, 0).plusDays(36);

        // when
        String path = DatePathProvider.toPath(daysWith36);

        // then
        assertEquals("00010", path);
    }


    @Test
    @DisplayName("경로를 날짜로 변환 테스트 - 기준일")
    void toDateTime_withBasePath_returnsCorrectDate() {
        // given
        String basePath = "00000";

        // when
        LocalDateTime dateTime = DatePathProvider.toDateTime(basePath);

        // then
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0, 0), dateTime);
    }

    @Test
    @DisplayName("경로를 날짜로 변환 테스트 - 기준일로부터 1일 후")
    void toDateTime_withOneDayAfterPath_returnsCorrectDate() {
        // given
        String oneDayAfterPath = "00001";

        // when
        LocalDateTime dateTime = DatePathProvider.toDateTime(oneDayAfterPath);

        // then
        assertEquals(LocalDateTime.of(2025, 1, 2, 0, 0, 0), dateTime);
    }

    @Test
    @DisplayName("경로를 날짜로 변환 테스트 - 잘못된 경로 길이")
    void toDateTime_withInvalidPathLength_throwsException() {
        // given
        String invalidPath = "0000"; // 5자가 아님

        // then
        assertThrows(IllegalArgumentException.class, () -> DatePathProvider.toDateTime(invalidPath));
    }


    @Test
    @DisplayName("현재 날짜의 시작시간 경로 테스트")
    void toStartOfDayPath_returnsPathForStartOfDay() {
        // given
        LocalDateTime dateTimeWithTime = LocalDateTime.of(2025, 3, 15, 14, 30, 45);
        LocalDateTime startOfDay = LocalDateTime.of(2025, 3, 15, 0, 0, 0);

        // when
        String startPath = DatePathProvider.toStartOfDayPath(dateTimeWithTime);
        String expectedPath = DatePathProvider.toPath(startOfDay);

        // then
        assertEquals(expectedPath, startPath);
    }

    @Test
    @DisplayName("현재 날짜의 종료시간 경로 테스트")
    void toEndOfDayPath_returnsPathForEndOfDay() {
        // given
        LocalDateTime dateTimeWithTime = LocalDateTime.of(2025, 3, 15, 14, 30, 45);
        LocalDateTime endOfDay = LocalDateTime.of(2025, 3, 15, 23, 59, 59, 999999999);

        // when
        String endPath = DatePathProvider.toEndOfDayPath(dateTimeWithTime);
        String expectedPath = DatePathProvider.toPath(endOfDay);

        // then
        assertEquals(expectedPath, endPath);
    }

    @Test
    @DisplayName("날짜 범위 생성 및 포함 여부 테스트")
    void createDateRange_andContains_worksCorrectly() {
        // given
        LocalDateTime start = LocalDateTime.of(2025, 2, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 2, 28, 23, 59, 59);
        LocalDateTime middle = LocalDateTime.of(2025, 2, 15, 12, 0, 0);
        LocalDateTime before = LocalDateTime.of(2025, 1, 15, 0, 0, 0);
        LocalDateTime after = LocalDateTime.of(2025, 3, 1, 0, 0, 0);

        // when
        DatePathProvider.DatePathRange range = DatePathProvider.createDateRange(start, end);
        String middlePath = DatePathProvider.toPath(middle);
        String beforePath = DatePathProvider.toPath(before);
        String afterPath = DatePathProvider.toPath(after);

        // then
        assertTrue(range.contains(middlePath));
        assertFalse(range.contains(beforePath));
        assertFalse(range.contains(afterPath));
    }

    @Test
    @DisplayName("여러 날짜에 대한 경로 생성 및 역변환 일관성 테스트")
    void consistencyTest_betweenToPathAndToDateTime() {
        // given
        LocalDateTime[] testDates = {
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                LocalDateTime.of(2025, 3, 15, 14, 30, 45),
                LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                LocalDateTime.of(2026, 6, 15, 12, 0, 0),
                LocalDateTime.of(2030, 1, 1, 0, 0, 0)
        };

        for (LocalDateTime date : testDates) {
            // when
            String path = DatePathProvider.toPath(date);
            LocalDateTime convertedBack = DatePathProvider.toDateTime(path);

            // then
            // 참고: 시간 정보는 손실될 수 있으므로 일 단위로만 비교
            assertEquals(date.toLocalDate(), convertedBack.toLocalDate(),
                    "Date: " + date + ", Path: " + path + ", Converted back: " + convertedBack);
        }
    }

}