package com.chitkarauniversity.mycabapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DriverSignUp extends AppCompatActivity {
    EditText txt_name,txt_pno,txt_password,txt_re_password,txt_taxino;
    Button btn_sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);
        txt_name=findViewById(R.id.driver_signup_txt_name);
        txt_pno=findViewById(R.id.driver_signup_txt_pno);
        txt_password=findViewById(R.id.driver_signup_txt_password);
        txt_re_password=findViewById(R.id.driver_signup_txt_re_password);
        txt_taxino=findViewById(R.id.driver_signup_txt_taxino);
        btn_sign_up=findViewById(R.id.driver_signup_btn_signup);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txt_re_password.getText().toString().equals(txt_password.getText().toString())) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, String> user = new HashMap<>();
                    user.put("name", txt_name.getText().toString());
                    user.put("pno", txt_pno.getText().toString());
                    user.put("password", txt_password.getText().toString());
                    user.put("taxino", txt_taxino.getText().toString());
                    // Add a new document with a generated ID
                    db.collection("drivers")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(DriverSignUp.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DriverSignUp.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }
                else
                    Toast.makeText(DriverSignUp.this, "Password and Re-Type Password does not match", Toast.LENGTH_SHORT).show();
            }
        });



    }
}