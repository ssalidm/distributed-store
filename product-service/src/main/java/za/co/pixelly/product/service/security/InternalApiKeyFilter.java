package za.co.pixelly.product.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_API_KEY_HEADER = "X-INTERNAL-API-KEY";

    @Value("${security.internal-api-key}")
    private String internalApiJey;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (!isStockEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedApiKey = request.getHeader(INTERNAL_API_KEY_HEADER);

        if (!matches(providedApiKey, internalApiJey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {
                        "success: false,
                        "status": 401,
                        "message: "Invalid or missing internal API key"
                    }
                    """);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isStockEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/products") && uri.contains("/stock/");
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
