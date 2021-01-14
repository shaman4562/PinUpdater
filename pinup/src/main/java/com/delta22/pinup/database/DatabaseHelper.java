package com.delta22.pinup.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH;
    private static String DB_NAME = "pins.db";
    private static final int SCHEMA = 1;
    public static final String TABLE_DOMAINS = "domains";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DOMAIN = "domain";
    public static final String COLUMN_PIN = "pin";
    public static final String COLUMN_CERT_EXPIRATION = "expiration";
    public static final String COLUMN_PIN_EXPIRATION = "pinExpiration";
    public static final String COLUMN_CHECK_PERIOD = "checkPeriod";
    public static final String COLUMN_UAE = "usingAfterExpiration";
    public static final String COLUMN_DISABLE_PIN = "disablePin";

    private static final int BUFFER_SIZE = 1024;

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.context = context;
        DB_PATH = context.getFilesDir().getPath() + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void create_db() {
        InputStream myInput;
        OutputStream myOutput;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getWritableDatabase();
                myInput = context.getAssets().open(DB_NAME);
                String outFileName = DB_PATH;
                myOutput = new FileOutputStream(outFileName);

                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SQLiteDatabase open() throws SQLException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}