package com.pan.skating.tieba;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.pan.skating.R;

public class TiebaActivity extends AppCompatActivity {

    private ListView listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tieba);

        listview= (ListView) findViewById(R.id.fm_list);

    }
}
