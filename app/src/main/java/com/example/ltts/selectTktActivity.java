package com.example.ltts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class selectTktActivity extends AppCompatActivity {

    private Button normalBook,seasonBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tkt);

        normalBook=findViewById(R.id.generalBooking);
        seasonBook=findViewById(R.id.seasonBooking);

        normalBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(selectTktActivity.this, bookTktActivity.class);
                startActivity(i);
                finish();
            }
        });

        seasonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(selectTktActivity.this, seasonTktActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}