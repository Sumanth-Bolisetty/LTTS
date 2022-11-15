package com.example.ltts;

public class ticketIdGenerator {
    private static Integer ticketId=10000;

    ticketIdGenerator()
    {
        ticketId+=1;
    }
    public Integer getTicketId() {
        return ticketId;
    }
}
