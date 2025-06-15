package com.dementor.global.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@Configuration
public class OpenSearchConfig {
    @Bean
    public OpenSearchClient openSearchClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("localhost", 9200, "http") // 반드시 http
        ).setHttpClientConfigCallback(httpClientBuilder -> {
            // SSL 완전 비활성화 (TrustAll)
            try {
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{
                        new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] xcs, String string) {}
                            public void checkServerTrusted(X509Certificate[] xcs, String string) {}
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        }
                }, null);
                httpClientBuilder.setSSLContext(sslContext);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return httpClientBuilder;
        });

        RestClient restClient = builder.build();

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        return new OpenSearchClient(transport);
    }
}
