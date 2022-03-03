package com.example.callog;

//java class to define db schema
public final class CallLogContract {
    //default constructor
    private CallLogContract(){

    }

    public static class CallLogEntry{//define inner class to define db schema
        // creating database
        //name of table
        public static final String TABLE_NAME = "CallLogTable";
        //columns of the table
        public final static String ID = "Id";
        public final static String PHONE_NUMBER = "PhoneNumber";
        public final static String CALL_TYPE = "CallType";
        public final static String CALL_DATE = "CallDate";
        public final static String CALL_DURATION = "CallDuration";

    }
}
