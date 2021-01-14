package com.delta22.pinup.confg;

import android.content.Context;
import com.delta22.pinup.Exceptions;
import com.delta22.pinup.database.Database;
import com.delta22.pinup.entry.Domain;
import com.delta22.pinup.request.UpdateHelper;
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.disposables.CompositeDisposable;
import static android.content.Context.MODE_PRIVATE;

public class Config {

    private final static String CONFIG_FILE_NAME = "pinfig";
    private final static String CONFIG_FILE_EXTENTION = "xml";
    private final static String CERTIFICATE_FILE_NAME = "pincert";
    private final static String CERTIFICATE_FILE_FOLDER = "raw";
    private final static String SHARED_PREF_NAME = "SharedPreferences";

    private final static String PINFIG_TAG = "pinfig";
    private final static String DOMAIN_TAG = "domain";
    private final static String NAME_TAG = "name";
    private final static String EXPIRATION_TAG = "cert-expiration";
    private final static String PIN_TAG = "pin";
    private final static String LIFETIME_TAG = "pin-expiration";
    private final static String CHECK_PERIOD_TAG = "check-period";
    private final static String UAE_TAG = "using-after-expiration";
    private final static String DISABLE_PIN_TAG = "disable-pin";

    private static Context context;
    private static String packageName;
    private static String pinServerAddress;
    private static ConfigTag currentTag = ConfigTag.DOMAIN;
    private static Domain currentDomain;
    private static int currentDomainId = 0;
    private static int domainsEndTagWasFound = 0;
    private static int pinCertId;

    private static List<Domain> configDomains = new ArrayList<>();
    private static List<Domain> localDomains = new ArrayList<>();

    public static CompositeDisposable compositeDisposable;

    public static void init(Context context, String packageName) throws Exception {
        Config.context = context;
        Config.packageName = packageName;
        pinCertId = Config.context.getResources().getIdentifier(CERTIFICATE_FILE_NAME, CERTIFICATE_FILE_FOLDER, Config.packageName);
        SharedPrefHelper.init(Config.context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE));
        Database.init(Config.context);
        readConfig();
        checkConfigChanges();
        UpdateHelper.updatePinsIfNeed();
    }

    private static void readConfig() throws Exception {
        try {
            XmlPullParser xpp = prepareXpp();
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG:
                        switch (xpp.getName()) {
                            case DOMAIN_TAG:
                                domainStart();
                                break;
                            case PINFIG_TAG:
                                String pinServerAddress = xpp.getAttributeValue(0);
                                savePinServerAddress(pinServerAddress);
                                break;
                            default:
                                break;
                        }
                    case XmlPullParser.END_TAG:
                        switch (xpp.getName()) {
                            case DOMAIN_TAG:
                                domainEnd();
                                break;
                            case NAME_TAG:
                                currentTag = ConfigTag.NAME;
                                break;
                            case EXPIRATION_TAG:
                                currentTag = ConfigTag.CERT_EXPIRATION;
                                break;
                            case PIN_TAG:
                                currentTag = ConfigTag.PIN;
                                break;
                            case LIFETIME_TAG:
                                currentTag = ConfigTag.PIN_EXPIRATION;
                                break;
                            case CHECK_PERIOD_TAG:
                                currentTag = ConfigTag.CHECK_PERIOD;
                                break;
                            case UAE_TAG:
                                currentTag = ConfigTag.UAE;
                                break;
                            case DISABLE_PIN_TAG:
                                currentTag = ConfigTag.DISABLE_PIN;
                                break;
                            default:
                                break;
                        }
                    case XmlPullParser.TEXT:
                        String text = xpp.getText();
                        if (text == null) {
                            break;
                        }
                        switch (currentTag) {
                            case NAME:
                                currentDomain.setDomain(text);
                                break;
                            case CERT_EXPIRATION:
                                currentDomain.setCertExpiration(Integer.parseInt(text));
                                break;
                            case PIN:
                                currentDomain.setPin(text);
                                break;
                            case PIN_EXPIRATION:
                                currentDomain.setPinExpiration(Integer.parseInt(text));
                                break;
                            case CHECK_PERIOD:
                                currentDomain.setCheckPeriod(Integer.parseInt(text));
                                break;
                            case UAE:
                                if (text.equals("false")) {
                                    currentDomain.setUsingAfterExpiration(0);
                                } else {
                                    currentDomain.setUsingAfterExpiration(1);
                                }
                                break;
                            case DISABLE_PIN:
                                if (text.equals("true")) {
                                    currentDomain.setDisablePin(1);
                                } else {
                                    currentDomain.setDisablePin(0);
                                }
                                break;
                            default:
                                break;
                        }
                }
                xpp.next();
            }
        } catch (Exception e) {
            throw new Exceptions.ReadConfigException("Error while config file parsing: " + e.getMessage());
        }
    }

    private static void domainStart() {
        currentDomain = new Domain(currentDomainId);
    }

    private static void savePinServerAddress(String address) {
        pinServerAddress = address;
    }

    private static void domainEnd() {
        domainsEndTagWasFound++;
        if (domainsEndTagWasFound == 2) {
            currentDomainId++;
            domainsEndTagWasFound = 0;
            configDomains.add(currentDomain);
        }
    }

    private static XmlPullParser prepareXpp() {
        int id = context.getResources().getIdentifier(CONFIG_FILE_NAME, CONFIG_FILE_EXTENTION, packageName);
        return context.getResources().getXml(id);
    }

    private static void checkConfigChanges() {
        localDomains = Database.getAllDomains();
        if (localDomains.size() == 0 && configDomains.size() > 0) {
            return;
        }
        for (int i = 0; i < configDomains.size(); i++) {
            Domain newDomain = configDomains.get(i);
            Domain oldDomain;
            try {
                oldDomain = Database.getDomainByDomainName(newDomain.getDomainName());
            } catch (Exception e) {
                Database.updateOrInsert(newDomain);
                return;
            }
            if (!oldDomain.equals(newDomain)) {
                Database.updateOrInsert(newDomain);
            }
        }
    }

    public static int getPinCertId() {
        return pinCertId;
    }

    public static Context getContext() {
        return context;
    }

    public static String getAppName() {
        return packageName;
    }

    public static String getPinDomain() {
        return pinServerAddress;
    }

    public enum ConfigTag {
        DOMAIN,
        NAME,
        CERT_EXPIRATION,
        PIN,
        PIN_EXPIRATION,
        CHECK_PERIOD,
        UAE,
        DISABLE_PIN
    }
}