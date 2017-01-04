package com.android.internal.telephony;

/**
 * Created by Marcus on 21/07/2016.
 */
public interface ITelephony {

    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}
