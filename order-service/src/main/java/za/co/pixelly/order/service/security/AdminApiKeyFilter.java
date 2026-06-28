package za.co.pixelly.order.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AdminApiKeyFilter extends OncePerRequestFilter {

    private static final String ADMIN_API_KEY_HEADER = "X-ADMIN-API-KEY";

    @Value("${security.admin-api-key}")
    private String adminApiKey;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (!isAdminEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedApiKey = request.getHeader(ADMIN_API_KEY_HEADER);

        if (!matches(providedApiKey, adminApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                      "success": false,
                      "status": 401,
                      "message": "Invalid or missing admin API key"
                    }
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAdminEndpoint(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/outbox-events");
    }

    private boolean matches(String provided, String expected) {
        if (provided == null || expected == null) {
            return false;
        }

        return MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8)
        );
    }
}