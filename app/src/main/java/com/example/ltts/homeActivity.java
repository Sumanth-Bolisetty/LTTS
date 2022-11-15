package com.example.ltts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class homeActivity extends AppCompatActivity {

    private Button bookTktBtn;
    private Button showTktsBtn;
    private Button logoutBtn;
    private Button wallet;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String userID;
    private ArrayList<Ticket> ticketsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        userID=((mAuth.getCurrentUser().getEmail()).split("@"))[0];

        ticketsList=new ArrayList<>();
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    Log.e("user",userID);
                    ArrayList<Long> ticketIDs=(ArrayList<Long>)task.getResult().child("Users").child(userID).child("tickets").getValue();
                    ticketIDs=new ArrayList<>(ticketIDs.subList(0,(int)ticketIDs.size()/2));
                    if(ticketIDs!=null)
                    {
                        for(Long ticketID:ticketIDs)
                        {
                            Log.e("ID",String.valueOf(ticketID));
                            Ticket tkt=task.getResult().child("Tickets").child(String.valueOf(ticketID)).getValue(Ticket.class);
                            ticketsList.add(tkt);
                        }
                    }

                }
                else
                {
                    Log.e("Failure","Failed to get data");
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("user",userID);
                ArrayList<Long> ticketIDs=(ArrayList<Long>)snapshot.child("Users").child(userID).child("tickets").getValue();
                if(ticketIDs!=null)
                {
                    for(Long ticketID:ticketIDs)
                    {
                        Log.e("ID",String.valueOf(ticketID));
                        Ticket tkt=snapshot.child("Tickets").child(String.valueOf(ticketID)).getValue(Ticket.class);
                        ticketsList.add(tkt);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Redirecting to Book Ticket Page
        bookTktBtn=findViewById(R.id.booktkt);
        bookTktBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBookTktActivity();
            }
        });

        //Redirecting to Show Tickets Page
        showTktsBtn=findViewById(R.id.showtkts);
        showTktsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent showTktsIntent=new Intent(homeActivity.this, showTktsActivity.class);
                showTktsIntent.putExtra("ticketsList",ticketsList);
                startActivity(showTktsIntent);
            }
        });

        wallet=findViewById(R.id.wallet);
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(homeActivity.this,walletActivity.class);
                startActivity(i);
            }
        });

        //Redirecting to Main Page
        logoutBtn=findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(homeActivity.this,"Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent logout=new Intent(homeActivity.this, loginActivity.class);
                startActivity(logout);
                finish();
            }
        });

    }
    public void openBookTktActivity()
    {
        Intent bookTktIntent=new Intent(this, selectTktActivity.class);
        startActivity(bookTktIntent);
    }

}