package com.xorazmapp.sayhundomladastur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Welcome extends AppCompatActivity {

    public static String Guruhi = null;

    Switch switch1;

    FirebaseDatabase database;

    DatabaseReference usersRef;

    FirebaseAuth mAuth;
    EditText ism, fam;
    Button tasdiqlash, chiqish;
    Spinner guruh;
    Spinner fan;


    String[] yunalish = {"MI22-1", "MI22-2", "MI22-3", "MI22-4", "MI22-5", "MI22-6", "MI22-7"};
    String[] fanlar = {"Kompyuter ta'minoti", "Dasturlash tili", "Axborot xavfsizligi", "Tarmoq texnologiyasi", "Web texnologiya"};

    ProgressDialog progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        switch1 = findViewById(R.id.switch1);

        progressBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        ism = findViewById(R.id.ism);
        fam = findViewById(R.id.fam);
        tasdiqlash = findViewById(R.id.tasdiq);
        chiqish = findViewById(R.id.chiqish);
        guruh = findViewById(R.id.spinner);
//        fan = findViewById(R.id.fan);

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guruh.getVisibility()==View.GONE){
                    guruh.setVisibility(View.VISIBLE);
                    switch1.setChecked(false);
                    switch1.setText("Men talabaman");
                }else {
                    guruh.setVisibility(View.GONE);
                    switch1.setChecked(true);
                    switch1.setText("Men o'qituvchiman");
                }

            }
        });

        tekshirAdmin();
        tekshirUser();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.guruh,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guruh.setAdapter(adapter);


//        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
//                this,
//                R.array.fan,
//                android.R.layout.simple_spinner_item
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        fan.setAdapter(adapter2);

        chiqish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(Welcome.this, RegActivity.class);
                startActivity(intent);

            }
        });

        tasdiqlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bazagayozish(
                        ism.getText().toString(),
                        fam.getText().toString()

                );
                tekshirAdmin();
                tekshirUser();
            }
        });
    }

    private void tekshirAdmin() {

        progressBar.setTitle("Foydalanuvchi bazadan tekshirilmoqda...");
        progressBar.show();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Admin");
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.dismiss();
//                    startActivity(new Intent(Welcome.this, Admin.class));
                    finish();

                } else {
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void tekshirUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (int i = 0; i < yunalish.length; i++) {
            int finalI = i;
            usersRef = FirebaseDatabase.getInstance().getReference("Talabalar").child(yunalish[i]);
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Guruhi = yunalish[finalI];
                        progressBar.dismiss();
                        startActivity(new Intent(Welcome.this, FanlarActivity.class));
                        finish();

                    } else {
                        progressBar.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void bazagayozish(String ism, String fam) {
        // Firebase bilan bog'lanish
        database = FirebaseDatabase.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        User user = new User(ism, fam);
        if (switch1.isChecked()){
            usersRef = database.getReference("Admin");
            usersRef.child(userId).setValue(user);
        }else{

            usersRef = database.getReference("Talabalar").
                    child(guruh.getSelectedItem().toString());

            usersRef.child(userId).setValue(user);
            for (int i = 0; i < fanlar.length; i++) {
                usersRef.child(userId).child(fanlar[i]).child("qabul").setValue(0);
                usersRef.child(userId).child(fanlar[i]).child("rad").setValue(0);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        tekshirAdmin();
        tekshirUser();


    }
}