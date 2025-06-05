package com.dementor.global.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.context.annotation.Configuration;
import org.opensearch.client.RestClient;

@Configuration
public class OpenSearchConfig {

    public OpenSearchClient openSearchClient() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new org.apache.http.auth.UsernamePasswordCredentials("admin", "G7!rT9@xQpLz")
        );

        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "https"))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();
        return new OpenSearchClient(
                new RestClientTransport(restClient, new JacksonJsonpMapper())
        );
    }

}
