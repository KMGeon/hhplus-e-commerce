package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DatePathProvider {
    private static final String CHARSET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int PATH_SIZE = 5;

    public static String toPath(LocalDateTime dateTime) {
        // 기준일시 설정 (2025-01-01 00:00:00)
        LocalDateTime baseDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        long days = ChronoUnit.DAYS.between(baseDateTime, dateTime);

        // 36진수로 변환하고 5자리로 패딩
        return convertToBase36(days);
    }

    public static LocalDateTime toDateTime(String path) {
        if (path.length() != PATH_SIZE) {
            throw new IllegalArgumentException("Invalid path length");
        }

        // 36진수를 10진수로 변환
        long days = convertFromBase36(path);
        return LocalDateTime.of(2025, 1, 1, 0, 0, 0).plusDays(days);
    }

    public static String toStartOfDayPath(LocalDateTime dateTime) {
        return toPath(dateTime.toLocalDate().atStartOfDay());
    }

    public static String toEndOfDayPath(LocalDateTime dateTime) {
        return toPath(dateTime.toLocalDate().atTime(23, 59, 59, 999999999));
    }

    public static DatePathRange createDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        String startPath = toPath(startDateTime);
        String endPath = toPath(endDateTime);
        return new DatePathRange(startPath, endPath);
    }

    private static String convertToBase36(long number) {
        StringBuilder result = new StringBuilder();
        do {
            result.insert(0, CHARSET.charAt((int)(number % 36)));
            number /= 36;
        } while (number > 0);

        while (result.length() < PATH_SIZE) {
            result.insert(0, '0');
        }
        return result.toString();
    }

    private static long convertFromBase36(String path) {
        long result = 0;
        for (char c : path.toCharArray()) {
            result = result * 36 + CHARSET.indexOf(c);
        }
        return result;
    }

    public record DatePathRange(String startPath, String endPath) {
        public boolean contains(String path) {
            return path.compareTo(startPath) >= 0 && path.compareTo(endPath) <= 0;
        }
    }
}