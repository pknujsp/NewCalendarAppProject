package com.zerodsoft.scheduleweather.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.RecyclerVIewAdapter.SearchAddressAdapter;
import com.zerodsoft.scheduleweather.Retrofit.DownloadData;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.Retrofit.QueryResponse.AddressResponseMeta;

import java.util.ArrayList;
import java.util.List;

public class SearchAddressActivity extends AppCompatActivity
{
    private ImageButton backButton;
    private EditText searchAddressEditText;
    private ImageButton searchAddressButton;
    private RecyclerView recyclerView;
    private SearchAddressAdapter adapter;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();

            List<AddressResponseDocuments> responseDocumentList = (ArrayList<AddressResponseDocuments>) bundle.getSerializable("documents");
            adapter.setAddressList(responseDocumentList);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        backButton = (ImageButton) findViewById(R.id.back_button);
        searchAddressEditText = (EditText) findViewById(R.id.search_address_edittext);
        searchAddressButton = (ImageButton) findViewById(R.id.search_address_button);
        recyclerView = (RecyclerView) findViewById(R.id.search_address_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new SearchAddressAdapter(new ArrayList<>(), SearchAddressActivity.this);
        recyclerView.setAdapter(adapter);

        searchAddressButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String address = searchAddressEditText.getText().toString();
                DownloadData.searchAddress(address, handler);
            }
        });

    }
}