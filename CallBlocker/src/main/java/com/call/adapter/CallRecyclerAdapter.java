package com.call.adapter;

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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import com.call.blocker.Contact;
import com.call.R;
import com.call.database.ContactDB;
import com.call.util.CurrentLocale;
import com.call.util.PublicKeys;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by Marcus on 28/07/2016.
 */

public class CallRecyclerAdapter
            extends RecyclerView.Adapter<CallRecyclerAdapter.ViewHolder> {

    private List<Contact> mContactList;
    private Context mContext;
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ContactDB mContactDB;
    private PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();

    public CallRecyclerAdapter(Context context, List<Contact> contactList) {
        this.mContactList = contactList;
        this.mContext = context;
        mContactDB = new ContactDB(context);

        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    public List<Contact> getContactList() {
        return mContactList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_item, parent, false);
        contactView.setBackgroundResource(mBackground);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Contact contact = mContactList.get(position);
        Phonenumber.PhoneNumber phoneNumber= null;

        try {
            phoneNumber = mPhoneNumberUtil.parse(contact.getNumber(),
                        CurrentLocale.getLocaleCountryCode(mContext));
        } catch (NumberParseException e) {
            Log.e(PublicKeys.TAG, e.getMessage());
        }

        String numberFormatted = mPhoneNumberUtil.format(phoneNumber,
                PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

        // Set item views based on your views and data model
        viewHolder.textViewNumber.setText(numberFormatted);
        viewHolder.textViewDescription.setText(contact.getDescription());
        viewHolder.checkBoxSms.setChecked(contact.isSmsBlocker());
        viewHolder.checkBoxCall.setChecked(contact.isCallBlocker());

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemAtPosition(viewHolder.getAdapterPosition(), contact);
            }
        });

        viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        viewHolder.checkBoxCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                contact.setCallBlocker(b);
                updateItemAtPosition(viewHolder.getAdapterPosition(), contact);
            }
        });
    }

    /**
     * Remove item at position predetermined!
     * @param position position at the click on RecyclerView
     * @param contact contact to remove
     */
    private void removeItemAtPosition(final int position, final Contact contact) {
        //final int correctPosition = getCorrectPosition(contact, position);

        mContactDB.remove(contact);
        mContactList.remove(position);
        notifyItemRemoved(position);
    }

    private void updateItemAtPosition(final int position, final Contact contact) {
        //final int correctPosition = getCorrectPosition(contact, position);

        mContactDB.update(contact);
        notifyItemChanged(position, contact);
    }

    @Deprecated
    /*
     * When the list of items is created, it will be assigned a position to each item regarding
     * the position of the list, but when the list is changed, the position of the list is
     * maintained to properly remove a list item you need to check where really this item it
     * is in the array representing the list.
     * @return correct position
     */
    private int getCorrectPosition (final Contact contact, final int position) {
        int correctPosition = -1;
        Contact c;

        if (position >= 0 && position < mContactList.size()
                && contact.getId() == mContactList.get(position).getId()) {

            correctPosition = position;
        } else {
            for (int i = 0; i < mContactList.size(); i++) {
                c = mContactList.get(i);
                if (c.getId() == contact.getId()) {
                    correctPosition = i;
                    break;
                }
            }
        }
        return correctPosition;
    }

    public void notifyItemInserted() {
        mContactList = mContactDB.getAll();
        notifyItemInserted(mContactList.size() - 1);
    }


    @Override
    public int getItemCount() {
        return mContactList.size();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewDescription;
        private TextView textViewNumber;
        private CheckBox checkBoxCall;
        private CheckBox checkBoxSms;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewDescription = (TextView) itemView.findViewById(R.id.list_item_textview_description);
            textViewNumber = (TextView) itemView.findViewById(R.id.list_item_textview_number);
            checkBoxCall = (CheckBox) itemView.findViewById(R.id.list_item_checkbox_call);
            checkBoxSms = (CheckBox) itemView.findViewById(R.id.list_item_checkbox_sms);
            view = itemView;
        }

    }

}
