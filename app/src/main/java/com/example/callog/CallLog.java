package com.example.callog;

public class CallLog {

    private String phNumber;
    private String callType;
    private String date;
    private String duration;

    public CallLog(String phNumber, String callType, String date, String duration) {
        this.phNumber = phNumber;
        this.callType = callType;
        this.date = date;
        this.duration = duration;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public String getCallType() {
        return callType;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }
}
