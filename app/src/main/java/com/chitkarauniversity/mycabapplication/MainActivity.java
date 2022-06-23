package com.chitkarauniversity.mycabapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    Spinner s;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s=findViewById(R.id.main_spinner);
        next=findViewById(R.id.main_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(s.getSelectedItem().toString().equals("Administrator"))
                {
                    Intent intent=new Intent(MainActivity.this,AdminLogin.class);
                    startActivity(intent);
                }
                else if(s.getSelectedItem().toString().equals("Driver"))
                {
                    Intent intent=new Intent(MainActivity.this,DriverLogin.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(MainActivity.this,ClientLogin.class);
                    startActivity(intent);
                }
            }
        });
    }
}