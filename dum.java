import io.netty.handler.ssl.SslContext;
import reactor.netty.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class CustomHttpClientConfiguration {

    @Bean
    public HttpClient customHttpClient() {
        SSLContext sslContext = customSSLContext();
        return HttpClient.create()
                .secure(ssl -> ssl.sslContext(new ReactorNettySslContext(sslContext)));
    }

    @Bean
    public SSLContext customSSLContext() {
        try {
            // Load your keystore (PFX file) and truststore (if needed)
            String keystorePath = "path/to/your.pfx";
            String keystorePassword = "yourKeystorePassword";

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream keystoreStream = new FileInputStream(keystorePath)) {
                keyStore.load(keystoreStream, keystorePassword.toCharArray());
            }

            // Create KeyManagerFactory and initialize it with the keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

            // Create the SSLContext with the KeyManagerFactory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException("Error creating custom SSLContext", e);
        }
    }

    public static class ReactorNettySslContext extends SslContext {
        private final SSLContext sslContext;

        ReactorNettySslContext(SSLContext sslContext) {
            super();
            this.sslContext = sslContext;
        }

        @Override
        public io.netty.handler.ssl.SslContext newEngine() {
            return new io.netty.handler.ssl.JdkSslContext(sslContext, true, io.netty.handler.ssl.JdkDefaultApplicationProtocolNegotiator.INSTANCE);
        }
    }
}
