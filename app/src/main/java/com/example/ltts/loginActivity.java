package com.example.ltts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class loginActivity extends AppCompatActivity {

    private EditText usernameEdt,passwordEdt;
    private Button loginBtn;
    private TextView registerTV;
    private FirebaseAuth mAuth;
    static String username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEdt=findViewById(R.id.username);
        passwordEdt=findViewById(R.id.password);
        loginBtn=findViewById(R.id.loginBtn);
        registerTV=findViewById(R.id.registerTV);
        mAuth=FirebaseAuth.getInstance();

        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ri=new Intent(loginActivity.this, registerActivity.class);
                startActivity(ri);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username=usernameEdt.getText().toString();
                password=passwordEdt.getText().toString();
                if(TextUtils.isEmpty(username) && TextUtils.isEmpty(password))
                {
                    Toast.makeText(loginActivity.this, "Enter the Credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(loginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                openHomeActivity();
                                finish();
                            }
                            else {
                                Toast.makeText(loginActivity.this, "Log In Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null)
        {
            openHomeActivity();
            this.finish();
        }
    }

    public void openHomeActivity()
    {
        Intent homePageIntent=new Intent(this, homeActivity.class);
        startActivity(homePageIntent);
    }
}