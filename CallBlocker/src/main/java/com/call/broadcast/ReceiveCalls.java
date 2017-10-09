package com.call.broadcast;

/*
 * Copyright 2016 Marcus Adriano
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

import com.call.blocker.Contact;
import com.call.database.ContactDB;
import com.call.util.CurrentLocale;
import com.call.util.PublicKeys;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by Marcus on 20/07/2016.
 */
public class ReceiveCalls extends BroadcastReceiver {

    private TelephonyManager mTelephonyManager;
    private Context mContext;
    private PhoneNumberUtil mPhoneNumberUtil;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.mContext = context;
        try {
            // TELEPHONY MANAGER class object to register one listener
            mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            //Create Listener
            final PhoneStateListener mPhoneStateListener = new MyPhoneStateListener();

            // Register listener for LISTEN_CALL_STATE
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

            mPhoneNumberUtil = PhoneNumberUtil.getInstance();
        } catch (Exception e) {
            Log.e(PublicKeys.TAG, e.getMessage());
        }
    }

    protected void endCall (Contact contactFounded) {
        try {
            Method getITelephony = mTelephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephony.setAccessible(true);
            Object ITelephony = getITelephony.invoke(mTelephonyManager);

            Method endCall = ITelephony.getClass().getDeclaredMethod("endCall");
            endCall.invoke(ITelephony);

            Log.i(PublicKeys.TAG, "The (" + contactFounded.getDescription() +
                                  "-" + contactFounded.getNumber() + ") was canceled.");

        } catch (Exception e) {
            Log.e(PublicKeys.TAG, e.getMessage());
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                try {
                    Phonenumber.PhoneNumber phoneNumber = mPhoneNumberUtil.parse(incomingNumber,
                            CurrentLocale.getLocaleCountryCode(mContext));
                    incomingNumber = mPhoneNumberUtil.format(phoneNumber,
                            PhoneNumberUtil.PhoneNumberFormat.E164);
                } catch (NumberParseException e) {
                    Log.e(PublicKeys.TAG, e.getMessage());
                    return;
                }

                ContactDB contactDB = new ContactDB(mContext.getApplicationContext());
                Contact contactFounded = contactDB.getBy(ContactDB.KEY_NUMBER, incomingNumber);

                if (contactFounded != null && contactFounded.isCallBlocker())
                    endCall(contactFounded);
            }
        }
    }
}
