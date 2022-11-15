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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class registerActivity extends AppCompatActivity {

    private EditText nameEdt,usernameEdt,passwordEdt, cfmpasswordEdt;
    private Button registerBtn;
    private TextView loginTV;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEdt=findViewById(R.id.name);
        usernameEdt=findViewById(R.id.username);
        passwordEdt=findViewById(R.id.password);
        cfmpasswordEdt =findViewById(R.id.Cfmpassword);
        registerBtn=findViewById(R.id.signupBtn);
        loginTV=findViewById(R.id.loginTV);
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(registerActivity.this, loginActivity.class);
                startActivity(i);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String name=nameEdt.getText().toString();
                String username=usernameEdt.getText().toString();
                String password=passwordEdt.getText().toString();
                String cfmpassword= cfmpasswordEdt.getText().toString();
                user=new User(name,500,new ArrayList<Integer>());
                if(!password.equals(cfmpassword))
                {
                    Toast.makeText(registerActivity.this,"Password and Confirm Password should be Same",Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(name) && TextUtils.isEmpty(username) && TextUtils.isEmpty(password) && TextUtils.isEmpty(cfmpassword))
                {
                    Toast.makeText(registerActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userID=(username.split("@"))[0];
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        databaseReference.child(userID).setValue(user);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(registerActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(registerActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(registerActivity.this, homeActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(registerActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}