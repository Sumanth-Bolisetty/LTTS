package com.example.ltts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class seasonTktActivity extends AppCompatActivity {

    private String [] stations={"Vizag","Marripalem","Gopalapatnam","Duvvada","Thadi","Anakapalle","Kasimkota","Bayyavaram","Elamanchili","Narsipatnam"};
    private Spinner fromStn,toStn;
    private ArrayAdapter<String> stationItems;
    private Button bookBtn;
    private ticketIdGenerator ticketIdGen;
    private Integer ticketID;
    private FirebaseAuth mAuth;
    private String userId;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_tkt);
        fromStn=findViewById(R.id.fromStn);
        toStn=findViewById(R.id.toStn);
        bookBtn=findViewById(R.id.bookBtn);
        stationItems=new ArrayAdapter<String>(this,R.layout.dropdown,stations);
        stationItems.setDropDownViewResource(R.layout.dropdown);
        fromStn.setAdapter(stationItems);
        toStn.setAdapter(stationItems);
        ticketIdGen=new ticketIdGenerator();

        mAuth=FirebaseAuth.getInstance();
        userId=((mAuth.getCurrentUser().getEmail()).split("@"))[0];
        firebaseDatabase=FirebaseDatabase.getInstance();

        bookBtn.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String from=fromStn.getSelectedItem().toString();
                String to=toStn.getSelectedItem().toString();
                if(from.equals(to))
                {
                    Toast.makeText(seasonTktActivity.this, "Enter Valid details", Toast.LENGTH_SHORT).show();
                }
                else {
                    Integer fare = calculateFare(from, to);
                    String status = "Pending";
                    ticketID = ticketIdGen.getTicketId();
                    Timestamp currentTime=new Timestamp(System.currentTimeMillis());
                    Ticket ticket = new Ticket(ticketID, from, to, true, fare, 1, 0, status, String.valueOf(currentTime));
                    ticket.setExpiryTime("2022-12-11");
                    Intent i = new Intent(seasonTktActivity.this, payment.class);
                    i.putExtra("ticket", ticket);
                    i.putExtra("ticketID", ticketID);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    public int calculateFare(String from,String to)
    {
        Integer baseFare=50;
        int x=0,y=0,fare=0;
        for(int i=0;i<stations.length;i++)
        {
            if(stations[i].equals(from))
                x=i;
            else if(stations[i].equals(to))
                y=i;
        }
        fare=(Math.abs(x-y))*baseFare;
        fare=Math.max(baseFare,(int)(fare/2));
        return fare;
    }
}