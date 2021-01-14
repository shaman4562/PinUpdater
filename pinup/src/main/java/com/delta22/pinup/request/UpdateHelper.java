package com.delta22.pinup.request;

import com.delta22.pinup.Exceptions;
import com.delta22.pinup.confg.Config;
import com.delta22.pinup.confg.SharedPrefHelper;
import com.delta22.pinup.database.Database;
import com.delta22.pinup.entry.Domain;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.delta22.pinup.confg.Config.compositeDisposable;

public class UpdateHelper {

    public static void updatePinsIfNeed() throws Exceptions.RequestDomainException {
        List<Domain> domains = Database.getAllDomains();
        for (Domain domain : domains) {
            String domainName = domain.getDomainName();
            int currentTime = currentTimeInMinutes();
            int nextUpdateTime = SharedPrefHelper.getUpdateTimeForDomain(domainName, 0) + domain.getCheckPeriod();
            if (nextUpdateTime <= currentTime ||
                    domain.getPinExpiration() <= currentTime ||
                    domain.getCertExpiration() <= currentTime) {
                UpdateHelper.requestDomainByNameAndSaveIt(domainName);
                int timeOfUpdate = currentTimeInMinutes();
                SharedPrefHelper.saveUpdateTimeForDomain(domainName, timeOfUpdate);
            }
        }
    }

    public static void requestDomainByNameAndSaveIt(String domainName) throws Exceptions.RequestDomainException {
        try {
            Disposable disposable = PinServerConnector
                    .getInstance(Config.getContext())
                    .getApi()
                    .requestDomenSingle(Config.getAppName(), domainName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Domain>() {
                                   @Override
                                   public void accept(Domain response) throws Exception {
                                       Database.updateOrInsert(response);
                                   }
                               },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable error) throws Exception {
                                    throw new Exceptions.RequestDomainException(domainName, error.getMessage());
                                }
                            });
            compositeDisposable.add(disposable);
        } catch (Exception e) {
            throw new Exceptions.RequestDomainException(domainName, e.getMessage());
        }
    }

    public static int currentTimeInMinutes() {
        return (int) (System.currentTimeMillis() / 1000 / 60);
    }
}