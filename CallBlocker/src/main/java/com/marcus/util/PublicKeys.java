package com.marcus.util;

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

import android.Manifest;

/**
 * Created by Marcus on 21/07/2016.
 * This class contains all public keys used in this project and keys are for used global!
 */
public final class PublicKeys {
    public static final String TAG = "cblocker";

    /**Permissions */
    public static final int PERMISSIONS_REQUEST_ALL = 1;
    public static final int PERMISSIONS_READ_CONTACTS = 2;
    public static final int PERMISSIONS_CALL_PHONE = 3;

    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
    };

    /** Activities */
    public static final int ACTIVITY_REQUEST_PERMISSIONS = 1;
    public static final int ACTIVITY_CONTACT_EDITOR = 2;

    /** SharedPreferences */
    public static final String SHARED_PHONE_NUMBER = "phoneNumber";
    public static final String SHARED_FIRST_IN = "firstIn";
    public static final String SHARED_LOCALE = "locale";
    public static final String SHARED_CARRIER = "carrier";
    public static final String SHARED_CARRIER_CODE = "carrierCode";
    public static final String SHARED_COUNTRY_CODE = "countryCode";

    public static final String SHARED_DEFAULT_PHONE_NUMBER = "";
    public static final int SHARED_DEFAULT_FIRST_IN = 0;
    public static final String SHARED_DEFAULT_LOCALE = "";
    public static final String SHARED_DEFAULT_CARRIER = "";
    public static final int SHARED_DEFAULT_CARRIER_CODE = 0;
    public static final int SHARED_DEFAULT_COUNTRY_CODE = 0;
}
