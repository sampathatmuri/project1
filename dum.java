package com.example.demo.test;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class WebClientConfig {

    @Value("${proxy.url}")
    private String proxyUrl;

    @Value("${proxy.port}")
    private int proxyPort;

    @Value("${proxy.username}")
    private String proxyUsername;

    @Value("${proxy.password}")
    private String proxyPassword;

    @Value("${ssl.pksPath}")
    private String pksPath;

    @Value("${ssl.pksPassword}")
    private String pksPassword;

    @Value("${ssl.jksPath}")
    private String jksPath;

    @Value("${ssl.jksPassword}")
    private String jksPassword;

    @Bean
    public WebClient customWebClient() throws Exception {
        HttpClient httpClient = HttpClient.create();

        if (proxyUrl != null && !proxyUrl.isEmpty()) {
            httpClient = httpClient.proxy(proxy -> proxy
                    .type(ProxyProvider.Proxy.HTTP)
                    .host(proxyUrl)
                    .port(proxyPort)
                    .username(proxyUsername)
                    .password(s -> proxyPassword));
        }

        KeyStore pksStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream pksInputStream = new FileInputStream(pksPath)) {
            pksStore.load(pksInputStream, pksPassword.toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(pksStore, pksPassword.toCharArray());

        KeyStore jksStore = KeyStore.getInstance("JKS");
        try (FileInputStream jksInputStream = new FileInputStream(jksPath)) {
            jksStore.load(jksInputStream, jksPassword.toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(jksStore);

        SslContext sslContext = SslContextBuilder.forClient()
                .keyManager(keyManagerFactory)
                .trustManager(trustManagerFactory)
                .build();;

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient.secure(t -> t.sslContext(sslContext))))
                .build();
    }
}
