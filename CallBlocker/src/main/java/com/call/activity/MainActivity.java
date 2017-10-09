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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.call.R;
import com.call.adapter.CallRecyclerAdapter;
import com.call.database.ContactDB;
import com.call.util.DividerItemDecoration;
import com.call.util.PublicKeys;

public class MainActivity extends AppCompatActivity {

    private final Context mContext = MainActivity.this;
    private ContactDB mContactDb;
    private CallRecyclerAdapter adapter;
    private TextView mTextViewNoItems;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactDb = new ContactDB(mContext);
        mTextViewNoItems = (TextView) findViewById(R.id.MainActivity_tv_noItems);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Check that the permissions were granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isPermissionsGranted()) {
                Intent intentRequestPermissions = new Intent(mContext, RequestPermissionsActivity.class);
                startActivityForResult(intentRequestPermissions, PublicKeys.ACTIVITY_REQUEST_PERMISSIONS);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case PublicKeys.ACTIVITY_CONTACT_EDITOR:
                adapter.notifyItemInserted();
                break;
        }
    }

    public void onFabClick (View v) {
        Intent intent = new Intent(mContext, ContactEditorActivity.class);
        intent.putExtra(ContactEditorActivity.EXTRA_EDITOR, ContactEditorActivity.EXTRA_TYPE_NEW);
        startActivityForResult(intent, PublicKeys.ACTIVITY_CONTACT_EDITOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    private void setupRecyclerView () {
        adapter = new CallRecyclerAdapter(mContext, mContactDb.getAll(ContactDB.KEY_DESCRIPTION));

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.MainActivity_recyclerView);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);

        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mTextViewNoItems.setVisibility(adapter.getContactList().isEmpty() ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                onChanged();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                onChanged();
            }
        });

        adapter.notifyDataSetChanged();
    }

    private boolean isPermissionsGranted() {
        if (ContextCompat.checkSelfPermission
                (mContext, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            return false;
        else if (ContextCompat.checkSelfPermission
                (mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)
            return false;
        else
            return true;
    }
}

