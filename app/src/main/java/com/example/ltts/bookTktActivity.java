package com.example.ltts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class bookTktActivity extends AppCompatActivity {

    private String [] stations={"Vizag","Marripalem","Gopalapatnam","Duvvada","Thadi","Anakapalle","Kasimkota","Bayyavaram","Elamanchili","Narsipatnam"};
    private Integer [] adultCounts={0,1,2,3,4};
    private Integer [] childCounts={0,1,2};
    private Spinner fromStn,toStn,adultCount,childCount;
    private ArrayAdapter<String> stationItems;
    private ArrayAdapter<Integer> adultCountItems,childCountItems;
    private Button bookBtn;
    private RadioGroup JourneyGroup;
    private ticketIdGenerator ticketIdGen;
    private Integer ticketID;
    private FirebaseAuth mAuth;
    private String userId;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_tkt);
        fromStn=findViewById(R.id.fromStn);
        toStn=findViewById(R.id.toStn);
        adultCount=findViewById(R.id.adultCount);
        childCount=findViewById(R.id.childCount);
        bookBtn=findViewById(R.id.bookBtn);
        JourneyGroup=findViewById(R.id.journeyType);
        stationItems=new ArrayAdapter<String>(this,R.layout.dropdown,stations);
        adultCountItems=new ArrayAdapter<Integer>(this,R.layout.dropdown,adultCounts);
        childCountItems=new ArrayAdapter<Integer>(this,R.layout.dropdown,childCounts);
        stationItems.setDropDownViewResource(R.layout.dropdown);
        adultCountItems.setDropDownViewResource(R.layout.dropdown);
        childCountItems.setDropDownViewResource(R.layout.dropdown);
        fromStn.setAdapter(stationItems);
        stationItems=new ArrayAdapter<String>(this,R.layout.dropdown,stations);
        toStn.setAdapter(stationItems);
        adultCount.setAdapter(adultCountItems);
        childCount.setAdapter(childCountItems);
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
                int journeyId=JourneyGroup.getCheckedRadioButtonId();
                RadioButton journey_radio_button=(RadioButton) findViewById(journeyId);
                Integer adult=Integer.parseInt(adultCount.getSelectedItem().toString());
                Integer child=Integer.parseInt(childCount.getSelectedItem().toString());
                boolean JourneyType;
                if(from.equals(to)||(adult==0&&child==0))
                {
                    Toast.makeText(bookTktActivity.this, "Enter Valid details", Toast.LENGTH_SHORT).show();
                }
                else {
                    JourneyType = journey_radio_button.getText().toString().equals("Single");
                    Integer fare = calculateFare(from, to, JourneyType, adult, child);
                    String status = "Pending";
                    ticketID = ticketIdGen.getTicketId();
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    Ticket ticket = new Ticket(ticketID, from, to, !JourneyType, fare, adult, child, status, currentTime.toString());

                    if (JourneyType) {
                        ticket.setExpiryTime((new Timestamp(currentTime.getTime() + (1000 * 60 * 60 * 3))).toString());
                    } else {
                        ticket.setExpiryTime((new Timestamp(currentTime.getTime() + (1000 * 60 * 60 * 9))).toString());
                    }
                    Intent i = new Intent(bookTktActivity.this, reviewActivity.class);
                    i.putExtra("ticket", ticket);
                    i.putExtra("ticketID", ticketID);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    public int calculateFare(String from,String to,Boolean journeyType,Integer adult,Integer child)
    {
        Integer baseFare=5;
        int x=0,y=0,farePP=0,fare=0;
        for(int i=0;i<stations.length;i++)
        {
            if(stations[i].equals(from))
                x=i;
            else if(stations[i].equals(to))
                y=i;
        }
        farePP=(Math.abs(x-y))*baseFare;
        fare+=farePP*adult;
        fare+=(int)(farePP/2)*child;
        if(!journeyType)
            fare+=fare;
        return fare;
    }
}