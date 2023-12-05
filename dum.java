package com.example.demo.test2;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

@Configuration
public class WebClientConfig {

    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.port}")
    private int proxyPort;

    @Value("${proxy.username}")
    private String username;

    @Value("${proxy.password}")
    private String password;

    @Value("${ssl.keystore.path}")
    private Resource keystorePath;

    @Value("${ssl.keystore.password}")
    private String keystorePassword;

    @Value("${ssl.truststore.path}")
    private Resource truststorePath;

    @Value("${ssl.truststore.password}")
    private String truststorePassword;

    @Bean
    public WebClient webClient()  {
        HttpClient httpClient = HttpClient.create()
                .secure(spec -> {
                    try {
                        spec.sslContext(sslContext());
                    } catch (GeneralSecurityException | IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build())
                .clientConnector(new ReactorClientHttpConnector(httpClient.proxy(proxySpec ->
                        proxySpec.
                                type(ProxyProvider.Proxy.HTTP)
                                .host(proxyHost)
                                .port(proxyPort)
                                .username(username)
                                .password(s -> password))))
                .build();
    }

    private SslContext sslContext() throws GeneralSecurityException, IOException {
        return SslContextBuilder.forClient()
                .keyManager(getKeyManagerFactory(keystorePath.getInputStream(), keystorePassword))
                .trustManager(getTrustManagerFactory(truststorePath.getInputStream(), truststorePassword))
                .build();
    }

    private javax.net.ssl.KeyManagerFactory getKeyManagerFactory(InputStream keystoreInputStream, String keystorePassword)
            throws GeneralSecurityException, IOException {
        java.security.KeyStore keyStore = java.security.KeyStore.getInstance("PKCS12");
        keyStore.load(keystoreInputStream, keystorePassword.toCharArray());

        javax.net.ssl.KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, keystorePassword.toCharArray());

        return kmf;
    }

    private javax.net.ssl.TrustManagerFactory getTrustManagerFactory(InputStream truststoreInputStream, String truststorePassword)
            throws GeneralSecurityException, IOException {
        java.security.KeyStore trustStore = java.security.KeyStore.getInstance("JKS");
        trustStore.load(truststoreInputStream, truststorePassword.toCharArray());

        javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);

        return tmf;
    }
}
