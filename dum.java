import reactor.netty.http.client.HttpClient;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class CustomHttpClientWithSSLContext {

    public HttpClient createCustomHttpClientWithSSLContext() throws Exception {
        SSLContext sslContext = createSSLContext();

        return HttpClient.create()
                .secure(ssl -> ssl.sslContext(new ReactorNettySslContext(sslContext)));
    }

    public SSLContext createSSLContext() throws Exception {
        String keystorePath = "path/to/your.pfx";
        String keystorePassword = "yourKeystorePassword";

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream keystoreStream = new FileInputStream(keystorePath)) {
            keyStore.load(keystoreStream, keystorePassword.toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return sslContext;
    }

    // ReactorNettySslContext inner class remains the same
}
