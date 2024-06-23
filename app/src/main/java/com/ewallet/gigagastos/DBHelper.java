package com.ewallet.gigagastos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDetailsDB";
    private static final String TABLE_NAME = "UserDetails";
    private static final String COL_ID = "id";
    private static final String COL_PHONE_NUMBER = "phone_number";
    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_LAST_NAME = "last_name";
    private static final String COL_BIRTHDAY = "birthday";
    private static final String COL_ADDRESS = "address";
    private static final String COL_EMAIL_ADDRESS = "email_address";
    private static final String COL_MPIN = "mpin";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PHONE_NUMBER + " TEXT, " +
                COL_FIRST_NAME + " TEXT, " +
                COL_LAST_NAME + " TEXT, " +
                COL_BIRTHDAY + " TEXT, " +
                COL_ADDRESS + " TEXT, " +
                COL_EMAIL_ADDRESS + " TEXT, " +
                COL_MPIN + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertContact(String phoneNumber, String firstName, String lastName, String birthday, String address, String email, String mpin) {
        // Remove any spaces from the phone number
        phoneNumber = phoneNumber.replaceAll("\\s+", "");

        // Add leading zero if necessary
        if (!phoneNumber.startsWith("0")) {
            phoneNumber = "0" + phoneNumber;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PHONE_NUMBER, phoneNumber);
        contentValues.put(COL_FIRST_NAME, firstName);
        contentValues.put(COL_LAST_NAME, lastName);
        contentValues.put(COL_BIRTHDAY, birthday);
        contentValues.put(COL_ADDRESS, address);
        contentValues.put(COL_EMAIL_ADDRESS, email);
        contentValues.put(COL_MPIN, mpin);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public String getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder data = new StringBuilder();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Check if the column exists before retrieving its value
                int phoneNumberIndex = cursor.getColumnIndex(COL_PHONE_NUMBER);
                if (phoneNumberIndex != -1) {
                    String phoneNumber = cursor.getString(phoneNumberIndex);
                    data.append("Phone Number: ").append(phoneNumber).append("\n");
                }

                int firstNameIndex = cursor.getColumnIndex(COL_FIRST_NAME);
                int lastNameIndex = cursor.getColumnIndex(COL_LAST_NAME);
                if (firstNameIndex != -1 && lastNameIndex != -1) {
                    String firstName = cursor.getString(firstNameIndex);
                    String lastName = cursor.getString(lastNameIndex);
                    data.append("Full Name: ").append(firstName).append(" ").append(lastName).append("\n");
                }

                int birthdayIndex = cursor.getColumnIndex(COL_BIRTHDAY);
                if (birthdayIndex != -1) {
                    String birthday = cursor.getString(birthdayIndex);
                    data.append("Birthday: ").append(birthday).append("\n");
                }

                int addressIndex = cursor.getColumnIndex(COL_ADDRESS);
                if (addressIndex != -1) {
                    String address = cursor.getString(addressIndex);
                    data.append("Address: ").append(address).append("\n");
                }

                int emailIndex = cursor.getColumnIndex(COL_EMAIL_ADDRESS);
                if (emailIndex != -1) {
                    String email = cursor.getString(emailIndex);
                    data.append("Email: ").append(email).append("\n");
                }

                int mpinIndex = cursor.getColumnIndex(COL_MPIN);
                if (mpinIndex != -1) {
                    String mpin = cursor.getString(mpinIndex);
                    data.append("MPIN: ").append(mpin).append("\n\n");
                }
            } while (cursor.moveToNext());
        }

        // Close cursor and return the data as a string
        if (cursor != null) {
            cursor.close();
        }
        return data.toString();
    }

    public boolean phoneNumberExists(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COL_PHONE_NUMBER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phoneNumber});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count > 0;
    }

    // Method to retrieve MPIN from the database based on phone number
    @SuppressLint("Range")
    public String getMPIN(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_MPIN + " FROM " + TABLE_NAME + " WHERE " + COL_PHONE_NUMBER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phoneNumber});
        String mpin = null;
        if (cursor != null && cursor.moveToFirst()) {
            mpin = cursor.getString(cursor.getColumnIndex(COL_MPIN));
            cursor.close();
        }
        return mpin;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COL_EMAIL_ADDRESS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count > 0;
    }
}
