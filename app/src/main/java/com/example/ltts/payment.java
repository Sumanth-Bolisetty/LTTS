package com.example.ltts;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class payment extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private Integer wallet;
    private String userId;
    private TextView walletTxt;
    private DatabaseReference databaseReference;
    private ArrayList<Integer> tickets;
    private Integer ticketID;
    private Ticket ticket;
    private Button walletPay,upiPay;
    final int UPI_PAYMENT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        userId=((mAuth.getCurrentUser().getEmail()).split("@"))[0];
        walletTxt=findViewById(R.id.walletBalance);
        walletPay=findViewById(R.id.walletPay);
        upiPay=findViewById(R.id.upiPay);
        ticket=(Ticket) getIntent().getSerializableExtra("ticket");
        ticketID= getIntent().getIntExtra("ticketID",0);

        ticket.setStatus("Booked");
        databaseReference.child("Tickets").child(String.valueOf(ticketID)).setValue(ticket);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wallet=((Long)snapshot.child("Users").child(userId).child("wallet").getValue()).intValue();
                Log.e("wallet",String.valueOf(wallet));
                walletTxt.setText(String.valueOf(wallet));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(payment.this, "Failed to book ticket! Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        walletPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wallet>=ticket.getFare())
                {
                    databaseReference.child("Users").child(userId).child("wallet").setValue(wallet-ticket.getFare());

                    // Update user data in users child
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            tickets=(ArrayList<Integer>)snapshot.child("Users").child(userId).child("tickets").getValue();
                            if(tickets==null)
                            {
                                tickets=new ArrayList<Integer>();
                            }
                            tickets.add(ticketID);

                            databaseReference.child("Users").child(userId).child("tickets").setValue(tickets);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(payment.this, "Failed to book ticket! Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Redirect to showTickets Page
                    Toast.makeText(payment.this, "Ticket Booked", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(payment.this,homeActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(payment.this, "Not Enough Balance! Recharge", Toast.LENGTH_SHORT).show();
                }
            }
        });

        upiPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name="Sumanth Bolisetty";
                String amount=ticket.getFare().toString();
                String upiId="bsumanth02@oksbi";
                String note="Ticket Payment";
                payUsingUpi(name,amount,upiId,note);
            }
        });
    }
    void payUsingUpi(String name,String amount,String upiId,String note)
    {
        Uri uri=Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa",upiId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();

        Intent upiPayIntent=new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser=Intent.createChooser(upiPayIntent,"Pay With");
        if(null!=chooser.resolveActivity(getPackageManager()))
        {
            startActivityForResult(chooser,UPI_PAYMENT);
        }
        else
        {
            Toast.makeText(this, "No UPI app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case UPI_PAYMENT:
                if(RESULT_OK==resultCode||resultCode==11){
                    if(data!=null){
                        String trxt=data.getStringExtra("response");
                        ArrayList<String> dataList=new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    }else{
                        ArrayList<String> dataList=new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                }else{
                    ArrayList<String> dataList=new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data){
        if(isConnectionAvailable(payment.this)){
            String str=data.get(0);
            Log.d("UPIPAY","upiPaymentDataOperation:"+str);
            String paymentCancel="";
            if(str==null)
                str="discard";
            String status="";
            String approvalRefNo="";
            String response[]=str.split("&");
            for(int i=0;i<response.length;i++)
            {
                String equalStr[]=response[i].split("=");
                if(equalStr.length>=2){
                    if(equalStr[0].toLowerCase().equals("Status".toLowerCase())){
                        status=equalStr[1].toLowerCase();
                    }
                    else if(equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase())||equalStr[0].toLowerCase().equals("txnRef".toLowerCase())){
                        approvalRefNo=equalStr[1];
                    }
                }
                else{
                    paymentCancel="Payment Cancelled by User";
                }
            }

            if(status.equals("success"))
            {
                Toast.makeText(this,"Transaction successful.",Toast.LENGTH_SHORT).show();
                Log.d("UPI","responseStr:"+approvalRefNo);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tickets=(ArrayList<Integer>)snapshot.child("Users").child(userId).child("tickets").getValue();
                        if(tickets==null)
                        {
                            tickets=new ArrayList<Integer>();
                        }
                        tickets.add(ticketID);
                        databaseReference.child("Users").child(userId).child("tickets").setValue(tickets);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(payment.this, "Failed to book ticket! Try Again", Toast.LENGTH_SHORT).show();
                    }
                });

                // Redirect to showTickets Page
                Toast.makeText(payment.this, "Ticket Booked", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(payment.this,homeActivity.class);
                startActivity(i);
                finish();
            }
            else if("Payment Cancelled by User".equals(paymentCancel)){
                Toast.makeText(this, paymentCancel, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Transaction failed.Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Internet Connection not Available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isConnectionAvailable(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo netInfo=connectivityManager.getActiveNetworkInfo();
            if(netInfo!=null && netInfo.isConnected() && netInfo.isConnectedOrConnecting()){
                return true;
            }
        }
        return false;
    }
}

