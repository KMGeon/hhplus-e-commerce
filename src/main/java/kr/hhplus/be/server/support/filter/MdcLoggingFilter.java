package kr.hhplus.be.server.support.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * MDC(Mapped Diagnostic Context)를 사용하여 로깅 정보를 추가하고
 * 요청/응답 본문을 포함한 상세 로깅을 수행하는 통합 필터
 */
@Slf4j
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String CLIENT_IP = "clientIp";
    private static final String HTTP_METHOD = "httpMethod";
    private static final String REQUEST_URI = "requestUri";
    private static final String USER_AGENT = "userAgent";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 요청/응답 본문을 캐싱하기 위한 래퍼 생성
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            // 요청 ID 생성 (UUID)
            String requestId = UUID.randomUUID().toString().replace("-", "");
            MDC.put(REQUEST_ID, requestId);

            // 클라이언트 IP 주소 추출
            String clientIp = getClientIp(requestWrapper);
            MDC.put(CLIENT_IP, clientIp);

            // HTTP 메소드 추출
            String method = requestWrapper.getMethod();
            MDC.put(HTTP_METHOD, method);

            // 요청 URI 추출
            String uri = requestWrapper.getRequestURI();
            MDC.put(REQUEST_URI, uri);

            // User-Agent 추출
            String userAgent = requestWrapper.getHeader("User-Agent");
            if (userAgent != null) {
                MDC.put(USER_AGENT, userAgent);
            }

            // 요청 시작 로그
            log.info("Request started: {} {}", method, uri);

            // 다음 필터 실행
            filterChain.doFilter(requestWrapper, responseWrapper);

        } finally {
            // 요청 로깅
            logRequest(requestWrapper);

            // 응답 로깅 및 성능 측정
            long duration = System.currentTimeMillis() - startTime;
            logResponse(responseWrapper, duration);

            // 중요: 응답 본문을 클라이언트에게 전송
            responseWrapper.copyBodyToResponse();

            // MDC 정보 제거
            MDC.clear();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String ip = MDC.get(CLIENT_IP);
        String method = MDC.get(HTTP_METHOD);
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {
            url += "?" + queryString;
        }

        String requestBody = getContent(request.getContentAsByteArray());

        log.info("[REQUEST] ID: {}, IP: {}, Method: {}, URL: {}, Body: {}",
                MDC.get(REQUEST_ID), ip, method, url, requestBody);
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();

        log.info("[RESPONSE] ID: {}, Duration: {}ms, Status: {}",
                MDC.get(REQUEST_ID), duration, status);
    }

    private String getContent(byte[] content) {
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "[EMPTY]";
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}