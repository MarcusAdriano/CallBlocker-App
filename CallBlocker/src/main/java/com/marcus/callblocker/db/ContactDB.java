package com.marcus.callblocker.db;

/*
 * Copyright 2017 Marcus Adriano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import com.marcus.callblocker.model.Contact;
import com.marcus.util.PublicKeys;

/**
 * Created by Marcus on 22/07/2016.
 *
 */
public class ContactDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DbBc";
    private static final int DATABASE_VERSION = 1;

    public static String KEY_ID = "id";
    public static String KEY_DESCRIPTION = "description";
    public static String KEY_NUMBER = "number";
    public static String KEY_CALLBLOCKER = "call";
    public static String KEY_SMSBLOCKER = "sms";
    public static String TABLE_NAME = "contactstable";

    public static int ERROR_ADD_ALREADY_EXIST_CONTACT = -1;

    private static final String[] TABLE_COLUMNS = {KEY_ID, KEY_DESCRIPTION, KEY_NUMBER, KEY_CALLBLOCKER, KEY_SMSBLOCKER};

    public ContactDB (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTATCS_TABLE =
                "CREATE TABLE " + TABLE_NAME +
                        "(" +
                            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                            KEY_DESCRIPTION + " TEXT, " +
                            KEY_NUMBER + " TEXT UNIQUE," +
                            KEY_CALLBLOCKER + " INTEGER," +
                            KEY_SMSBLOCKER + " INTEGER" +
                        ")";


        db.execSQL(CREATE_CONTATCS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    /**
     * Add new contact.
     * @param contact <b>contact</b> to add in database
     * @return return <b>id</b> of the contact in database or return -1 when ocurred an <b>error</b>.
     * <br > Use const ERROR_ADD_ALREADY_EXIST_CONTACT to verify if there is an error, because this
     * error it's possible when you try insert an existent contact in database
     */
    public long add(Contact contact){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        int callBlocker = contact.isCallBlocker() ? 1 : 0;
        int smsBlocker = contact.isSmsBlocker() ? 1 : 0;

        values.put(KEY_DESCRIPTION, contact.getDescription());
        values.put(KEY_NUMBER, contact.getNumber());
        values.put(KEY_CALLBLOCKER, callBlocker);
        values.put(KEY_SMSBLOCKER, smsBlocker);
        // 3. insert
        long result = db.insert(TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();

        if (result != ERROR_ADD_ALREADY_EXIST_CONTACT)
            Log.i(PublicKeys.TAG, "Contact add: " + contact.getDescription()
                                              + "(" + contact.getNumber() + "). Callblocker: " + callBlocker
                                                                            + " SMSBlocker: " + smsBlocker);
        else
            Log.i(PublicKeys.TAG, "Contact already " + contact.getDescription()
                                             + "-" + contact.getNumber() + " exist!");

        return result;
    }

    /**
     * Get an existing contact by id.
     * @param KEY to filter database's search
     * @param VALUE  value of the <b>KEY</b> to filter database's search
     * @return a contact reference or null if the contact with id isn't existing
     */
    public Contact getBy(final String KEY, final String VALUE){
        if ((!KEY.equals(KEY_ID) && !KEY.equals(KEY_NUMBER))
            || (KEY.equals("") || VALUE.equals(""))) {
            Log.e(PublicKeys.TAG, "KEY and/or VALUE is invalid!");
            return null;
        }

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        TABLE_COLUMNS, // b. column names
                        KEY + " = ?", // c. selections
                        new String[] { String.valueOf(VALUE) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        try {
            if (cursor != null) {
                cursor.moveToFirst();

                final int callBlocker = cursor.getInt(3);
                final int smsBlocker = cursor.getInt(4);

                // 4. build contact object
                Contact contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setDescription(cursor.getString(1));
                contact.setNumber(cursor.getString(2));
                contact.setCallBlocker(callBlocker == 1);
                contact.setSmsBlocker(smsBlocker == 1);

                Log.i(PublicKeys.TAG, "Get contact by " + KEY + " (" + VALUE + "): " + contact.getDescription() + "("
                        + contact.getNumber() + ") Callblocker: " + callBlocker +
                        " SMSBlocker: " + smsBlocker);

                return contact;
            } else {
                Log.i(PublicKeys.TAG, "Get contact by " + KEY + " (" + VALUE + "): " + "CONTACT NOT FOUND!");
                return null;
            }
        } finally {
            cursor.close();
            db.close();
        }
    }

    /**
     * Get all contacts presents in database.
     * @return list of contacts
     */
    public List<Contact> getAll() {
        List<Contact> contacts = new ArrayList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_NAME;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build contact and add it to list
        Contact contact;
        if (cursor.moveToFirst()) {
            do {
                final int callBlocker = cursor.getInt(3);
                final int smsBlocker = cursor.getInt(4);

                // 4. build contact object
                contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setDescription(cursor.getString(1));
                contact.setNumber(cursor.getString(2));
                contact.setCallBlocker(callBlocker == 1);
                contact.setSmsBlocker(smsBlocker == 1);

                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        Log.i(PublicKeys.TAG, "Get all contacts (" + contacts.size() + ")");
        cursor.close();
        db.close();
        return contacts;
    }

    public List<Contact> getAll(final String orderBy) {
        List<Contact> contacts = new ArrayList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + orderBy;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build contact and add it to list
        Contact contact;
        if (cursor.moveToFirst()) {
            do {
                final int callBlocker = cursor.getInt(3);
                final int smsBlocker = cursor.getInt(4);

                // 4. build contact object
                contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setDescription(cursor.getString(1));
                contact.setNumber(cursor.getString(2));
                contact.setCallBlocker(callBlocker == 1);
                contact.setSmsBlocker(smsBlocker == 1);

                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        Log.i(PublicKeys.TAG, "Get all contacts (" + contacts.size() + ")");
        cursor.close();
        db.close();
        return contacts;
    }

    /**
     * Update an existing contact
     * @param contact contact to update data
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
     */
    public int update(Contact contact) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        int callBlocker = contact.isCallBlocker() ? 1 : 0;
        int smsBlocker = contact.isSmsBlocker() ? 1 : 0;

        values.put(KEY_DESCRIPTION, contact.getDescription());
        values.put(KEY_NUMBER, contact.getNumber());
        values.put(KEY_CALLBLOCKER, callBlocker);
        values.put(KEY_SMSBLOCKER, smsBlocker);


        // 3. updating row
        int result = db.update(TABLE_NAME, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[] { String.valueOf(contact.getId()) }); //selection args

        // 4. close
        db.close();

        Log.i(PublicKeys.TAG, "Contact updated: " + contact.getDescription()
                + "(" + contact.getNumber() + "). Callblocker: " + callBlocker
                + " SMSBlocker: " + smsBlocker);

        return result;
    }


    /**
     *
     * @param contact contatct to remove
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
     */
    public int remove(Contact contact) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        int result = db.delete(TABLE_NAME   ,
                KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });

        // 3. close
        db.close();
        if (result > 0)
            Log.i(PublicKeys.TAG, "Contact(" + contact.getId() + "-" + contact.getDescription() + ") was deleted!");
        return result;
    }

}
