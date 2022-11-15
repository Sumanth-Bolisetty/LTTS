package com.example.ltts;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class Ticket implements Serializable
{
    private Integer ticketId;
    private String fromStn;
    private String toStn;
    private boolean twoWay;
    private Integer fare;
    private Integer adultCount;
    private Integer childCount;
    private String status;
    private String generationTime;
    private String expiryTime;

    public Ticket(){}

    public Ticket(Integer ticketId, String fromStn, String toStn, boolean twoWay, Integer fare, Integer adultCount, Integer childCount, String status, String generationTime) {
        this.ticketId=ticketId;
        this.fromStn = fromStn;
        this.toStn = toStn;
        this.twoWay = twoWay;
        this.fare = fare;
        this.adultCount = adultCount;
        this.childCount = childCount;
        this.status = status;
        this.generationTime = generationTime;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public String getFromStn() {
        return fromStn;
    }

    public void setFromStn(String fromStn) {
        this.fromStn = fromStn;
    }

    public String getToStn() {
        return toStn;
    }

    public void setToStn(String toStn) {
        this.toStn = toStn;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public Integer getFare() {
        return fare;
    }

    public void setFare(Integer fare) {
        this.fare = fare;
    }

    public Integer getAdultCount() {
        return adultCount;
    }

    public void setAdultCount(Integer adultCount) {
        this.adultCount = adultCount;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(String generationTime) {
        this.generationTime = generationTime;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }
}
