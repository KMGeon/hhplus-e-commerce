package kr.hhplus.be.server.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * MDC(Mapped Diagnostic Context)를 사용하여 로깅 정보를 추가하는 필터
 * 모든 HTTP 요청에 대해 요청 ID, 클라이언트 IP, HTTP 메소드, 경로 등의 정보를 MDC에 추가함
 */
@Slf4j
@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String CLIENT_IP = "clientIp";
    private static final String HTTP_METHOD = "httpMethod";
    private static final String REQUEST_URI = "requestUri";
    private static final String USER_AGENT = "userAgent";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 요청 ID 생성 (UUID)
            String requestId = UUID.randomUUID().toString().replace("-", "");
            MDC.put(REQUEST_ID, requestId);
            
            // 클라이언트 IP 주소 추출
            String clientIp = getClientIp(request);
            MDC.put(CLIENT_IP, clientIp);
            
            // HTTP 메소드 추출
            MDC.put(HTTP_METHOD, request.getMethod());
            
            // 요청 URI 추출
            MDC.put(REQUEST_URI, request.getRequestURI());
            
            // User-Agent 추출
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null) {
                MDC.put(USER_AGENT, userAgent);
            }
            
            // 요청 시작 로그
            log.info("Request started");
            
            // 다음 필터 실행
            filterChain.doFilter(request, response);
            
            // 요청 종료 로그
            log.info("Request completed with status code: {}", response.getStatus());
        } finally {
            // MDC 정보 제거
            MDC.clear();
        }
    }
    
    /**
     * 클라이언트 IP 주소를 추출하는 메소드
     * X-Forwarded-For 헤더를 확인하고, 없으면 RemoteAddr을 사용
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For에는 여러 IP가 쉼표로 구분되어 있을 수 있음
            // 첫 번째 IP가 원래 클라이언트 IP
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
} 