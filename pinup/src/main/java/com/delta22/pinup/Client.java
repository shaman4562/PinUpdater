package com.delta22.pinup;

import androidx.annotation.NonNull;

import com.delta22.pinup.confg.Config;
import com.delta22.pinup.database.Database;
import com.delta22.pinup.entry.Domain;
import com.delta22.pinup.request.PinServerConnector;
import com.delta22.pinup.request.UpdateHelper;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;

public class Client {

    private final static String PIN_PREFIX = "sha256/";

    public static OkHttpClient.Builder builder(@NonNull String domainName) throws Exception {
        String pin = PIN_PREFIX + getPinForDomain(domainName);
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(domainName, pin)
                .build();
        return httpBuilder.certificatePinner(certificatePinner);
    }

    private static String getPinForDomain(@NonNull String domainName) throws Exception {
        Domain domain;
        try {
            domain = Database.getDomainByDomainName(domainName);
        } catch (Exceptions.NoSuchDomainException e) {
            return requestPin(domainName);
        }
        if (domain.getDisablePin() == 1) {
            return requestPin(domainName);
        }
        if (domain.getPinExpiration() <= UpdateHelper.currentTimeInMinutes() &&
                domain.getUsingAfterExpiration() == 0) {
            throw new Exceptions.UsingOldCertificateException(domain.getDomainName());
        }
        if (domain.getCertExpiration() <= UpdateHelper.currentTimeInMinutes()) {
            throw new Exceptions.ExpiredCertificateException(domain.getDomainName());
        }
        return Database.getPinByDomainName(domainName);
    }

    private static String requestPin(@NonNull String domainName) throws Exception {
        return PinServerConnector
                .getInstance(Config.getContext())
                .getApi()
                .requestDomenSingle(Config.getAppName(), domainName)
                .blockingGet()
                .getPin();
    }
}
