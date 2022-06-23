package com.chitkarauniversity.mycabapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class ClientLogin extends AppCompatActivity {

    EditText txt_pno,txt_password;
    Button btn_login,btn_signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_login);
        txt_pno=findViewById(R.id.client_login_pno);
        txt_password=findViewById(R.id.client_login_password);
        btn_login=findViewById(R.id.client_login_btn_login);
        btn_signup=findViewById(R.id.client_login_btn_signup);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("clients")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int f=0;
                                    for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        Map<String,Object> client=document.getData();
                                        if(client.get("pno").toString().equals(txt_pno.getText().toString()) && client.get("password").toString().equals(txt_password.getText().toString())) {
                                            Toast.makeText(ClientLogin.this, "Valid User Success", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(ClientLogin.this,ClientHome.class);
                                            startActivity(intent);
                                            f=1;
                                            break;
                                        }

                                    }
                                    if(f==0)
                                        Toast.makeText(ClientLogin.this, "Invalid Id or password", Toast.LENGTH_SHORT).show();
                                }
                                else {



                                }
                            }
                        });


            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ClientLogin.this,ClientSignUp.class);
                startActivity(intent);
            }
        });
    }
}