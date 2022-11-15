package com.example.ltts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class reviewActivity extends AppCompatActivity {
    TextView from,to,journeyType,adult,child,fare;
    Button edit,proceed;
    Integer ticketID;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        from=findViewById(R.id.from);
        to=findViewById(R.id.to);
        journeyType=findViewById(R.id.journeytype);
        adult=findViewById(R.id.adult);
        child=findViewById(R.id.child);
        edit=findViewById(R.id.editBtn);
        proceed=findViewById(R.id.proceedBtn);
        fare=findViewById(R.id.fare);

        Ticket ticket=(Ticket) getIntent().getSerializableExtra("ticket");
        ticketID=(Integer) getIntent().getIntExtra("ticketID",0);
        from.setText(ticket.getFromStn());
        to.setText(ticket.getToStn());
        if(ticket.isTwoWay()) {
            journeyType.setText("Two way");
        }else{
            journeyType.setText("One way");
        }
        adult.setText(String.valueOf(ticket.getAdultCount()));
        child.setText(String.valueOf(ticket.getChildCount()));
        fare.setText(String.valueOf(ticket.getFare()));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(reviewActivity.this, bookTktActivity.class);
                startActivity(i);
                finish();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(reviewActivity.this, payment.class);
                i.putExtra("ticket",ticket);
                i.putExtra("ticketID",ticketID);
                startActivity(i);
                finish();
            }
        });
    }

}