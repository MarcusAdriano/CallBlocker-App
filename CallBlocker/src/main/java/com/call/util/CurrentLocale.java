package com.call.util;

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
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.Locale;

import com.call.R;

/**
 * Created by Marcus on 02/08/2016.
 */
public final class CurrentLocale {

    public static Locale getCurrent(Context context) {
        final String SHARED_PREFERENCES_FILE_KEY =
                context.getResources().getString(R.string.SharedPreferences);

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);

        final String local = sharedPreferences.getString(PublicKeys.SHARED_LOCALE, PublicKeys.SHARED_DEFAULT_LOCALE);

        if (local.equals(PublicKeys.SHARED_DEFAULT_LOCALE))
            return Locale.getDefault();
        else {
            String lang[] = local.split(";");
            return new Locale(lang[0], lang[1]);
        }
    }

    public static String getLocaleCountryCode (Context context) {
        Locale local = getCurrent(context);
        return local.toString().split("_")[1];
    }

}
