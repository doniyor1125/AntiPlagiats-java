package com.xorazmapp.sayhundomladastur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FanlarActivity extends AppCompatActivity {

    RelativeLayout relativeLayout1, relativeLayout2;

    static FanId fanId;

    FirebaseDatabase database;
    DatabaseReference usersRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanlar);


        fanId = new FanId(0);

        relativeLayout1 = findViewById(R.id.relaty);
        relativeLayout2 = findViewById(R.id.relaty2);

        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fanId.setId(1);
                startActivity(new Intent(FanlarActivity.this, CheckWord.class));
            }
        });

        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fanId.setId(2);
                startActivity(new Intent(FanlarActivity.this, CheckWord.class));
            }
        });

    }
}