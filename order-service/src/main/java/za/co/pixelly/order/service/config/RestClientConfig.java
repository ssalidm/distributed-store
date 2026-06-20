package za.co.pixelly.order.service.config;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Bean
    RestClient productRestClient(
            @Value("${services.product-service.base-url}") String productServiceBaseUrl,
            @Value("${services.product-service.connect-timeout-ms}") long connectTimeoutMs,
            @Value("${services.product-service.read-timeout-ms}") long readTimeoutMs
    ) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .withReadTimeout(Duration.ofMillis(readTimeoutMs));

        return RestClient.builder()
                .baseUrl(productServiceBaseUrl)
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
                .requestInterceptor(((request, body, execution) -> {
                    String correlationId = MDC.get(CORRELATION_ID_KEY);

                    if (correlationId != null && !correlationId.isBlank()) {
                        request.getHeaders().set(CORRELATION_ID_HEADER, correlationId);
                    }

                    return execution.execute(request, body);
                }))
                .build();
    }
}
