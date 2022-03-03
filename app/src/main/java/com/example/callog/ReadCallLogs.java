package com.example.callog;

//this class provides methods to read call logs from default database and returns the values in arraylist

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReadCallLogs {
    private static final String TAG = "ReadCallLogs";

    private Context context;
    //protected ArrayList<String> allRowData = new ArrayList<>();//array list to store data of call logs
    private ArrayList<com.example.callog.CallLog> callLogs = new ArrayList<>();


    //constructor
    public ReadCallLogs(Context context) {
        this.context = context;
    }

    public ArrayList<com.example.callog.CallLog> readCallLogsData() {
        //get cursor from the default database. missing permission is supressed because the permission will be taken in the main activity
        //and this method will be called from main activity
        @SuppressLint("MissingPermission")
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        //set column index in different integers from the cursor
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        //access data in different strings from the cursor
        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            //convert call date from string into date type
            Date callDateTime = new Date(Long.valueOf(callDate));
            //format date type
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY");
            //store formatted date type in the string dateString
            String dateString = simpleDateFormat.format(callDateTime);
            String callDuration = cursor.getString(duration);
            //create string to store call type
            String dir = null;
            int dirCode = Integer.parseInt(callType);
            switch (dirCode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    dir = "REJECTED";
                    break;
            }

            Log.d(TAG, "readCallLogsData: " + phNumber);
            Log.d(TAG, "readCallLogsData: " + dateString);
            //create object from CallLog and pass the above strings into it as parameters
            com.example.callog.CallLog callLog = new com.example.callog.CallLog(phNumber, dir, dateString, callDuration);
            //add data to callLogs arraylist
            callLogs.add(callLog);
        }
        //close the cursor
        cursor.close();
        //return the arraylist
        return callLogs;
    }

}
