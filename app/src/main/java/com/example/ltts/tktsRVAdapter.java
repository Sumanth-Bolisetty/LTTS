package com.example.ltts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class tktsRVAdapter extends RecyclerView.Adapter<tktsRVAdapter.ViewHolder> {
    private final ArrayList<Ticket> ticketsList;
    private final Context context;

    public tktsRVAdapter(ArrayList<Ticket> ticketsList,Context context) {
        this.ticketsList = ticketsList;
        this.context=context;
    }

    @NonNull
    @Override
    public tktsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tktsRVAdapter.ViewHolder holder, int position) {
        Ticket ticket=ticketsList.get(position);
        holder.ticketID.setText(String.valueOf(ticket.getTicketId()));
        holder.from.setText(ticket.getFromStn());
        holder.to.setText(ticket.getToStn());
        holder.adult.setText(String.valueOf(ticket.getAdultCount()));
        holder.child.setText(String.valueOf(ticket.getChildCount()));
        holder.fare.setText(String.valueOf(ticket.getFare()));
        if(ticket.isTwoWay())
        {
            holder.twoWay.setText(String.valueOf("Two-Way Journey"));
        }
        String expiryTime=ticket.getExpiryTime();
        if(expiryTime.length()>16)
        {
            expiryTime=expiryTime.substring(0,16);
        }
        holder.expiry.setText(expiryTime);
    }

    @Override
    public int getItemCount() {
        return ticketsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        final TextView ticketID,from,to,adult,child,fare,twoWay,expiry;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ticketID=itemView.findViewById(R.id.ticketID);
            from=itemView.findViewById(R.id.fromStn);
            to=itemView.findViewById(R.id.toStn);
            adult=itemView.findViewById(R.id.adult);
            child=itemView.findViewById(R.id.child);
            fare=itemView.findViewById(R.id.fare);
            twoWay=itemView.findViewById(R.id.twoWay);
            expiry=itemView.findViewById(R.id.expiry);
        }
    }


}
