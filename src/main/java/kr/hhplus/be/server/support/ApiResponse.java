package kr.hhplus.be.server.support;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    public static final ApiResponse<Integer> SUCCESS_WITH_INT_ONE = success(1);
    public static final ApiResponse<Integer> SUCCESS_WITH_INT_ZERO = success(0);

    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("", "", data);
    }

    public static <T> ApiResponse<Integer> maskToInteger(T data) {
        return data != null ? SUCCESS_WITH_INT_ONE : SUCCESS_WITH_INT_ZERO;
    }

    public static ApiResponse<Object> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static ApiResponse<Object> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.getCode(), message, null);
    }
}