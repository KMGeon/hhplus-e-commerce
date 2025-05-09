package kr.hhplus.be.server.support.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // Common
    NOT_FOUND_EXCEPTION("A-001", "존재하지 않습니다"),
    VALIDATION_EXCEPTION("A-002", "잘못된 요청입니다"),
    ALL_EXCEPTION_ERROR("A-003", "오류가 발생하였습니다.");

    private final String code;
    private final String message;

}
