package com.example.ltts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class walletActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String userId;
    private Button walletHisBtn,addMoneyBtn;
    TextView walletBalance;
    private EditText addWalletAmt;
    private String amount;
    private Integer wallet;
    final int UPI_PAYMENT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        mAuth= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        userId=((mAuth.getCurrentUser().getEmail()).split("@"))[0];


        walletBalance=findViewById(R.id.walletBalance);
        addMoneyBtn=findViewById(R.id.addMoneyBtn);
        addWalletAmt=findViewById(R.id.addBalanceAmt);

        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    wallet=((Long)task.getResult().child("Users").child(userId).child("wallet").getValue()).intValue();
                    Log.e("wallet",String.valueOf(wallet));
                    walletBalance.setText(String.valueOf(wallet));
                }
            }
        });

        addMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=addWalletAmt.getText().toString();
                String name="B Sumanth";
                String upiId="bsumanth02@ybl";
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
        if(isConnectionAvailable(walletActivity.this)){
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

                databaseReference.child("Users").child(userId).child("wallet").setValue(wallet+Integer.valueOf(amount));

                // Redirect to showTickets Page
                Toast.makeText(walletActivity.this, "Money Added", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(walletActivity.this,homeActivity.class);
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