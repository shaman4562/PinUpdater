package com.delta22.pinup.request;

import android.content.Context;
import com.delta22.pinup.confg.Config;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PinServerConnector {

    private static PinServerConnector mInstance;
    private Retrofit mRetrofit;

    private PinServerConnector(Context context) throws Exception {
        OkHttpClient client = getClientForPinServer(context);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Config.getPinDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(client)
                .build();
    }

    public static PinServerConnector getInstance(Context context) throws Exception {
        if (mInstance == null) {
            mInstance = new PinServerConnector(context);
        }
        return mInstance;
    }

    public PostRequest getApi() {
        return mRetrofit.create(PostRequest.class);
    }

    public static OkHttpClient getClientForPinServer(Context context) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream cert = context.getResources().openRawResource(Config.getPinCertId());
        Certificate ca;
        try {
            ca = cf.generateCertificate(cert);
        } finally {
            cert.close();
        }
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslContext.getSocketFactory());
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        return builder.build();
    }
}
