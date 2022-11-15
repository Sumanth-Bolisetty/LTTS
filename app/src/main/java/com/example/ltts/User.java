package com.example.ltts;

import java.util.ArrayList;

public class User
{
    private String name;
    private Integer wallet=100;
    private ArrayList<Integer> tickets=new ArrayList<Integer>();

    public User()
    {

    }
    public User(String name, Integer wallet, ArrayList<Integer> tickets) {
        this.name = name;
        this.wallet=wallet;
        this.tickets = tickets;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWallet() {
        return wallet;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public ArrayList<Integer> getTickets() {
        return tickets;
    }

    public void setTickets(ArrayList<Integer> tickets) {
        this.tickets = tickets;
    }
}
