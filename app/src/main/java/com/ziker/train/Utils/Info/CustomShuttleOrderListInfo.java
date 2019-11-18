package com.ziker.train.Utils.Info;

import java.util.List;

public class CustomShuttleOrderListInfo {
    /**
     * Id : 1
     * PhoneNumber : 13594714375
     * StartSite : 青年路市博物馆
     * EndSite : 青年路市博物馆
     * BusDate : ["2019-10-20"]
     * Ticket : 5
     * Flag : 1
     */

    private int Id;
    private String PhoneNumber;
    private String StartSite;
    private String EndSite;
    private int Ticket;
    private int Flag;
    private List<String> BusDate;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getStartSite() {
        return StartSite;
    }

    public void setStartSite(String StartSite) {
        this.StartSite = StartSite;
    }

    public String getEndSite() {
        return EndSite;
    }

    public void setEndSite(String EndSite) {
        this.EndSite = EndSite;
    }

    public int getTicket() {
        return Ticket;
    }

    public void setTicket(int Ticket) {
        this.Ticket = Ticket;
    }

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int Flag) {
        this.Flag = Flag;
    }

    public List<String> getBusDate() {
        return BusDate;
    }

    public void setBusDate(List<String> BusDate) {
        this.BusDate = BusDate;
    }
}
