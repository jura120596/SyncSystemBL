package com.example.fordecosport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    private TextView idTV, passwordTV;
    private Button loginBut;
    private String loginId, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBut = findViewById(R.id.loginBt);
        idTV = findViewById(R.id.idTv);
        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginId = idTV.getText().toString();

                NoDb.user = Integer.valueOf(loginId);

            }
        });
    }
}