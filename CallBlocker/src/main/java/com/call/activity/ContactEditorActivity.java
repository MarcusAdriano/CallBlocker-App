package com.call.activity;

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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.call.R;
import com.call.blocker.Contact;
import com.call.database.ContactDB;
import com.call.util.PublicKeys;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.Locale;

import com.call.util.CurrentLocale;
import com.google.i18n.phonenumbers.Phonenumber;

public class ContactEditorActivity extends AppCompatActivity {

    public static final String EXTRA_EDITOR = "contactEditor";
    public static final String EXTRA_CONTACT_ID = "contactId";
    public static final int EXTRA_TYPE_NEW = 1;
    public static final int EXTRA_TYPE_UPDATE = 2;
    public static final int EXTRA_EDITOR_DEFAULT = EXTRA_TYPE_NEW;
    public static final int EXTRA_CONTACT_ID_DEFAULT = -1;
    private Context mContext = ContactEditorActivity.this;
    private EditText mEditTextPhoneNumber;
    private EditText mEditTextDescription;
    private CheckBox mCheckBoxBlocked;
    private Phonenumber.PhoneNumber mPhoneNumber;
    private static PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
    private Locale mCurrentLocale;
    private FloatingActionButton mFab;

    private int extra;
    private int mContactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_editor);
        setResult(RESULT_CANCELED);

        Bundle contents = getIntent().getExtras();
        extra = contents.getInt(EXTRA_EDITOR, EXTRA_EDITOR_DEFAULT);
        mContactId = contents.getInt(EXTRA_CONTACT_ID, EXTRA_CONTACT_ID_DEFAULT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int ACTIVITY_TITLE = extra == EXTRA_TYPE_NEW ?
                R.string.NewContact : R.string.UpdateContact;

        getSupportActionBar().setTitle(ACTIVITY_TITLE);

        mEditTextPhoneNumber = (EditText) findViewById(R.id.ContactActivity_edittext_phonenumber);
        mEditTextDescription = (EditText) findViewById(R.id.ContactActivity_edittext_description);
        mCheckBoxBlocked = (CheckBox) findViewById(R.id.ConctactEditorActivity_check_contact_block);
        mFab = (FloatingActionButton) findViewById(R.id.ContactEditor_fab);

        /* Simple format for numbers */
        if (Build.VERSION.SDK_INT >=  21)
            mEditTextPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mCurrentLocale = CurrentLocale.getCurrent(mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        final int resMenu = extra == EXTRA_TYPE_NEW ?
                R.menu.menu_contact_editor_new : R.menu.menu_contact_editor_update;
        menuInflater.inflate(resMenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isAnyError () {
        final String descriptionText = mEditTextDescription.getText().toString().trim();
        String phoneNumberText = mEditTextPhoneNumber.getText().toString().trim();

        if (descriptionText.equals("")) {
            descriptionIncorrectInput();
            return true;
        }

        if (phoneNumberText.equals("")) {
            phoneNumberIncorrectInput();
            return true;
        }

        try {
            mPhoneNumber = mPhoneNumberUtil.parse(phoneNumberText, CurrentLocale.getLocaleCountryCode(mContext));
        } catch (NumberParseException e) {
            phoneNumberIncorrectInput();
            Log.i(PublicKeys.TAG, e.getMessage());
            return true;
        }

        final boolean isValidNumber = mPhoneNumberUtil.isValidNumber(mPhoneNumber);

        if (!isValidNumber) {
            phoneNumberIncorrectInput();
        }

        return !isValidNumber;
    }

    private void phoneNumberIncorrectInput() {
        mEditTextPhoneNumber.requestFocus();
        mEditTextPhoneNumber.setError(getResources().getString(R.string.ErrorPhoneNumberIncorrect));
    }

    private void descriptionIncorrectInput() {
        mEditTextDescription.requestFocus();
        mEditTextDescription.setError(getResources().getString(R.string.ErrorDescriptionNull));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.menu_action_new_contact_pick_contact:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFabClick (View view) {
        mFab.setEnabled(false);
        try {
            if (isAnyError())
                return;
            else
                addOrUpdateContact();
        } finally {
            mFab.setEnabled(true);
        }

    }

    private void addOrUpdateContact() {
        final String descriptionText = mEditTextDescription.getText().toString().trim();
        final String phoneNumberText = mPhoneNumberUtil.format(mPhoneNumber,
                PhoneNumberUtil.PhoneNumberFormat.E164);
        final boolean isBlocked = mCheckBoxBlocked.isChecked();

        ContactDB contactDB = new ContactDB(mContext);
        Contact contact = new Contact(descriptionText, phoneNumberText, isBlocked, false);

        long result;

        if (mContactId != EXTRA_CONTACT_ID_DEFAULT) {
            contact.setId(mContactId);
            result = contactDB.update(contact);
        } else {
            result = contactDB.add(contact);
        }

        /*
        Contact already exist, case this is called for add new contact and the contact's phone number
        already exist in database or this is called for contact's updates and the new modifications
        already exist in database.
         */
        if (result == ContactDB.ERROR_ADD_ALREADY_EXIST_CONTACT || result == 0) {
            final String errorMessage = getResources().getString(R.string.ErrorContactAlreadyExist);
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), errorMessage, Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK)
            NavUtils.navigateUpFromSameTask(this);
        return super.onKeyDown(keyCode, event);
    }
}
