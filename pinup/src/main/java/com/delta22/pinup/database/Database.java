package com.delta22.pinup.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import com.delta22.pinup.Exceptions;
import com.delta22.pinup.entry.Domain;
import java.util.ArrayList;
import java.util.List;

import static com.delta22.pinup.database.DatabaseHelper.COLUMN_CERT_EXPIRATION;
import static com.delta22.pinup.database.DatabaseHelper.COLUMN_CHECK_PERIOD;
import static com.delta22.pinup.database.DatabaseHelper.COLUMN_DISABLE_PIN;
import static com.delta22.pinup.database.DatabaseHelper.COLUMN_DOMAIN;
import static com.delta22.pinup.database.DatabaseHelper.COLUMN_ID;
import static com.delta22.pinup.database.DatabaseHelper.COLUMN_PIN;
import static com.delta22.pinup.database.DatabaseHelper.COLUMN_PIN_EXPIRATION;
import static com.delta22.pinup.database.DatabaseHelper.COLUMN_UAE;
import static com.delta22.pinup.database.DatabaseHelper.TABLE_DOMAINS;

public final class Database {

    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase db;
    private static Cursor cursor;

    public static void init(Context context) {
        databaseHelper = new DatabaseHelper(context);
        databaseHelper.create_db();
        db = databaseHelper.open();
    }

    public static void close() {
        db.close();
        databaseHelper.close();
        cursor.close();
    }

    public static List<Domain> getAllDomains() {
        cursor = db.rawQuery("SELECT * FROM " + TABLE_DOMAINS, null);
        return collectDomains(cursor);
    }

    public static String getPinByDomainName(@NonNull String domainName) throws Exceptions.NoSuchPinException {
        cursor = db.rawQuery("SELECT * FROM " + TABLE_DOMAINS + " WHERE " +
                COLUMN_DOMAIN + " = '" + domainName + "'", null);
        String pin;
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            pin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PIN));
        } else {
            throw new Exceptions.NoSuchPinException(domainName);
        }
        return pin;
    }

    public static Domain getDomainByDomainName(@NonNull String domainName) throws Exceptions.NoSuchDomainException {
        cursor = db.rawQuery("SELECT * FROM " + TABLE_DOMAINS + " WHERE " +
                COLUMN_DOMAIN + " = '" + domainName + "'", null);
        Domain domain;
        if (cursor.getCount() == 0) {
            throw new Exceptions.NoSuchDomainException(domainName);
        } else {
            domain = getDomainByDomainName(cursor, domainName);
        }
        return domain;
    }

    public static void replaceDomain(Domain domain) {
        delete(domain.getDomainName());
        insertDomain(domain);
    }

    public static void insertDomain(Domain domain) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, domain.getId());
        cv.put(COLUMN_DOMAIN, domain.getDomainName());
        cv.put(COLUMN_PIN, domain.getPin());
        cv.put(COLUMN_CERT_EXPIRATION, domain.getCertExpiration());
        cv.put(COLUMN_PIN_EXPIRATION, domain.getPinExpiration());
        cv.put(COLUMN_CHECK_PERIOD, domain.getCheckPeriod());
        cv.put(COLUMN_UAE, domain.getUsingAfterExpiration());
        cv.put(COLUMN_DISABLE_PIN, domain.getDisablePin());
        db.insert(TABLE_DOMAINS, null, cv);
    }

    public static void updateOrInsert(Domain domain) {
        cursor = db.rawQuery("SELECT * FROM " + TABLE_DOMAINS + " WHERE " +
                COLUMN_DOMAIN + " = '" + domain.getDomainName() + "'", null);
        if (cursor.getCount() > 0) {
            replaceDomain(domain);
        } else {
            insertDomain(domain);
        }
    }

    public static void delete(@NonNull String domainName) {
        db.delete(TABLE_DOMAINS, COLUMN_DOMAIN + " = ?", new String[]{domainName});
    }

    private static List<Domain> collectDomains(Cursor cursor) {
        List<Domain> domains = new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String domain = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOMAIN));
            String pin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PIN));
            int certExpiration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CERT_EXPIRATION));
            int pinExpiration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PIN_EXPIRATION));
            int checkPeriod = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHECK_PERIOD));
            int uae = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UAE));
            int disablePin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISABLE_PIN));
            domains.add(new Domain(id, domain, pin, certExpiration, pinExpiration, checkPeriod, uae, disablePin));
        }
        return domains;
    }

    private static Domain getDomainByDomainName(Cursor cursor, @NonNull String domainName) {
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String domain = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOMAIN));
        String pin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PIN));
        int certExpiration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CERT_EXPIRATION));
        int pinExpiration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PIN_EXPIRATION));
        int checkPeriod = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHECK_PERIOD));
        int uae = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UAE));
        int disablePin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISABLE_PIN));
        return new Domain(id, domain, pin, certExpiration, pinExpiration, checkPeriod, uae, disablePin);
    }
}