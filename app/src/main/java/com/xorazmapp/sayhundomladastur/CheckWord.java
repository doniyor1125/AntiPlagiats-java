package com.xorazmapp.sayhundomladastur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class CheckWord extends AppCompatActivity {

    private static final int DOC_REQUEST_CODE = 2;

    private static final int PDF_REQUEST_CODE = 1;

    private StorageReference storageReference;

    String[] fanlar = {"Axborot xavfsizligi","Dasturlash tili","Kompyuter ta'minoti", "Tarmoq texnologiyasi", "Web texnologiya"};


    String downloadUrl;

    Uri selectedFileUri;
    Button mBtn;

    Boolean tekshir = true;
    LinearLayout layout;

    PythonClass antiPlagiat;

    ImageView imageView;

    TextView textView, textView2;

    ProgressDialog progressBar;

    FirebaseStorage storage;
    StorageReference storageRef;
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_word);

        storageReference = FirebaseStorage.getInstance().getReference();

        random = new Random();

        antiPlagiat = new PythonClass();

        mBtn = findViewById(R.id.sending);
        textView = findViewById(R.id.text);
        imageView = findViewById(R.id.imageView);

        progressBar = new ProgressDialog(this);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startCheck();
                startActivity(new Intent(CheckWord.this, FanlarActivity.class));
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tekshir == true) {
                    selectPDF();
                }

                tekshir = false;
            }
        });

    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pdf faylni tanlang"), PDF_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri pdfUri = data.getData();
            uploadPDF(pdfUri);
        }
    }

    private void uploadPDF(Uri pdfUri) {
        progressBar.setTitle("Fayl yuklanish jarayonida...");
        progressBar.show();
//        layout.setEnabled(false);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        int n = FanlarActivity.fanId.getId();
        int m = random.nextInt(100000);
        boolean rost = antiPlagiat.pythonClass(pdfUri);
        if (rost==true) {
            StorageReference reference = storageReference.child("Fayllar/" + fanlar[n - 1] + String.valueOf(m) + ".pdf");

            reference.putFile(pdfUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.dismiss();
                            Toast.makeText(CheckWord.this, "Fayl muvaffaqiyatli yuklandi", Toast.LENGTH_SHORT).show();
                            //                        layout.setEnabled(false);
                            startActivity(new Intent(CheckWord.this, FanlarActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.dismiss();
                            Toast.makeText(CheckWord.this, "Fayl yuklashda xatolik", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

//    private boolean antiPlagiat(Uri pdfUri){
//
//        return false;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CheckWord.this, FanlarActivity.class));
        finish();
    }
}