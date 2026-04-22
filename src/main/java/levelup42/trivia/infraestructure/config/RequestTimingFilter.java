package levelup42.trivia.infraestructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTimingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestTimingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        long startTime = System.nanoTime();

        MDC.put("requestId", requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            String user = resolveUser();
            String requestUri = buildRequestUri(request);

            log.info(
                    "request_id={} method={} uri={} status={} duration_ms={} user={} remote_ip={} forwarded_for={}",
                    requestId,
                    request.getMethod(),
                    requestUri,
                    response.getStatus(),
                    elapsedMs,
                    user,
                    resolveRemoteIp(request),
                    request.getHeader("X-Forwarded-For")
            );
            MDC.remove("requestId");
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return requestId.trim();
    }

    private String resolveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        String userName = authentication.getName();
        if (userName == null || userName.isBlank() || "anonymousUser".equalsIgnoreCase(userName)) {
            return "anonymous";
        }

        return userName;
    }

    private String buildRequestUri(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + queryString;
    }

    private String resolveRemoteIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}