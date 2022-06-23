package com.chitkarauniversity.mycabapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
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
public class DriverLogin extends AppCompatActivity {
    EditText txt_taxi_no,txt_password;
    Button btn_login,btn_signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        txt_taxi_no=findViewById(R.id.driver_login_taxi_no);
        txt_password=findViewById(R.id.driver_login_password);
        btn_login=findViewById(R.id.driver_login_btn_login);
        btn_signup=findViewById(R.id.driver_login_btn_signup);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("drivers")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int f=0;
                                    for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        Map<String,Object> client=document.getData();
                                        if(client.get("taxino").toString().equals(txt_taxi_no.getText().toString()) && client.get("password").toString().equals(txt_password.getText().toString())) {
                                            Toast.makeText(DriverLogin.this, "Valid User Success", Toast.LENGTH_SHORT).show();
                                            SharedPreferences prefs=getSharedPreferences("mytaxi",MODE_PRIVATE);
                                            SharedPreferences.Editor edit=prefs.edit();
                                            edit.putString("taxino",client.get("taxino").toString());
                                            edit.putString("pno",client.get("pno").toString());
                                            edit.putString("name",client.get("name").toString());
                                            edit.commit();

                                            Intent intent=new Intent(DriverLogin.this,DriverHome.class);
                                            startActivity(intent);
                                            f=1;
                                            break;
                                        }

                                    }
                                    if(f==0)
                                        Toast.makeText(DriverLogin.this, "Invalid Id or password", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(DriverLogin.this, "Please wait or try later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DriverLogin.this,DriverSignUp.class);
                startActivity(intent);
            }
        });
    }
}