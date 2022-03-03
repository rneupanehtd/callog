package com.example.callog;

//java class to create table and upgrade table.
//Note: table is updated explicitly by creating different methods!!!

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class CallLogDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "CallLogDBHelper";
    public Context context;
    public static final String DATABASE_NAME = "call_log_db";
    public static final int DATABASE_VERSION = 1;

    //db query to create table
    public static final String CREATE_TABLE = "CREATE TABLE " + CallLogContract.CallLogEntry.TABLE_NAME + "("
            + CallLogContract.CallLogEntry.PHONE_NUMBER + " VARCHAR,  "
            + CallLogContract.CallLogEntry.CALL_TYPE + " VARCHAR, "
            + CallLogContract.CallLogEntry.CALL_DATE + " VARCHAR , "
            + CallLogContract.CallLogEntry.CALL_DURATION + " VARCHAR, PRIMARY KEY ( "+ CallLogContract.CallLogEntry.PHONE_NUMBER
            +","+ CallLogContract.CallLogEntry.CALL_DATE +") );";
            //phone number and call date are used as composite key

    //db query to drop table if exists
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + CallLogContract.CallLogEntry.TABLE_NAME;

    //default constructor
    public CallLogDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d("Database operation", "Database created...");//display logcat message
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create table with the query defined in CREATE_TABLE final string
        //sqLiteDatabase.execSQL(DROP_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d("Database operation", "Table created...");//display logcat message
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //delete table if already exists
        sqLiteDatabase.execSQL(DROP_TABLE);
        //create table again
        onCreate(sqLiteDatabase);
    }

    public void addAllLogs() {

        //copy the arraylist containing call log details from ReadCallLogs into new list callLogData
        ArrayList<CallLog> callLogData = new ArrayList<>(new ReadCallLogs(context).readCallLogsData());

        Log.d(TAG, "addAllLogs: " + callLogData.size());

        //get writable database
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        //create content values to store data from arraylist
        ContentValues contentValues = new ContentValues();

        //read through the elements of arraylist callLogData and place in the contentvalues by respective column
        for (int i = 0; i < callLogData.size(); i++) {
            CallLog callLog = callLogData.get(i);
            contentValues.put(CallLogContract.CallLogEntry.PHONE_NUMBER, callLog.getPhNumber());
            contentValues.put(CallLogContract.CallLogEntry.CALL_TYPE, callLog.getCallType());
            contentValues.put(CallLogContract.CallLogEntry.CALL_DATE, callLog.getDate());
            contentValues.put(CallLogContract.CallLogEntry.CALL_DURATION, callLog.getDuration());

            //insert content values into database
            sqLiteDatabase.insert(CallLogContract.CallLogEntry.TABLE_NAME, null, contentValues);
        }

        Log.d("Database operation", "size of contentValues: " + String.valueOf(contentValues.size()));
        //insert content values into database table
//        sqLiteDatabase.insert(CallLogContract.CallLogEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor getAllLogs() {
        //create query
        String query = "SELECT * FROM " + CallLogContract.CallLogEntry.TABLE_NAME;
        //run query
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        //display logcat message
        Log.d("Database operation", "Data extration successful...");
        //return results
        return cursor;
    }
    /*public void deleteAllLogs(){
        String query = "DELETE FROM "+ CallLogContract.CallLogEntry.TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        sqLiteDatabase.execSQL("vacuum");
        cursor.close();
    }*/

    public int[] getCallTypeTotal(){
        //run query
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] argumentIn={"INCOMING"};
        Cursor cursorIncoming = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE "+ CallLogContract.CallLogEntry.CALL_TYPE + " = ? ", argumentIn);
        String[] argumentOut = {"OUTGOING"};
        Cursor cursorOutgoing = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE "+ CallLogContract.CallLogEntry.CALL_TYPE + " = ? ", argumentOut);
        String[] argumentMissed = {"MISSED"};
        Cursor cursorMissed = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE "+ CallLogContract.CallLogEntry.CALL_TYPE + " = ? ", argumentMissed);
        String[] argumentRej = {"REJECTED"};
        Cursor cursorRejected = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE "+ CallLogContract.CallLogEntry.CALL_TYPE + " = ? ", argumentRej);

        int incomingTotal = cursorIncoming.getCount();
        int outgoingTotal = cursorOutgoing.getCount();
        int missedTotal = cursorMissed.getCount();
        int rejectedTotal = cursorRejected.getCount();

        int[] totalCallsType = {incomingTotal, outgoingTotal, missedTotal, rejectedTotal};

        Log.d("total call logs", String.valueOf(totalCallsType[1]));

        cursorIncoming.close();
        cursorOutgoing.close();
        cursorMissed.close();
        cursorRejected.close();

        sqLiteDatabase.close();

        return totalCallsType;
    }

    public int[] getCallTypeTotalBetweenDates(String startDate, String endDate){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] argumentDateIn={startDate, endDate, "INCOMING"};
        Cursor cursorIncoming = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE ("+ CallLogContract.CallLogEntry.CALL_DATE + " BETWEEN ? AND ? ) " +
                "AND "+ CallLogContract.CallLogEntry.CALL_TYPE+" =? ", argumentDateIn);

        String[] argumentDateOut={startDate, endDate, "OUTGOING"};
        Cursor cursorOutgoing = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE ("+ CallLogContract.CallLogEntry.CALL_DATE + " BETWEEN ? AND ? ) " +
                "AND "+ CallLogContract.CallLogEntry.CALL_TYPE+" =? ", argumentDateOut);

        String[] argumentDateMissed={startDate, endDate, "MISSED"};
        Cursor cursorMissed = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE ("+ CallLogContract.CallLogEntry.CALL_DATE + " BETWEEN ? AND ? ) " +
                " AND "+ CallLogContract.CallLogEntry.CALL_TYPE+" =? ", argumentDateMissed);

        String[] argumentDateRejected={startDate, endDate, "REJECTED"};
        Cursor cursorRejected = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE ("+ CallLogContract.CallLogEntry.CALL_DATE + " BETWEEN ? AND ? ) " +
                "AND "+ CallLogContract.CallLogEntry.CALL_TYPE+" =? ", argumentDateRejected);

        int incomingTotalBetweenDates = cursorIncoming.getCount();
        int outgoingTotalBetweenDates = cursorOutgoing.getCount();
        int missedTotalBetweenDates = cursorMissed.getCount();
        int rejectedTotalBetweenDates = cursorRejected.getCount();

        int[] totalCallsType = {incomingTotalBetweenDates , outgoingTotalBetweenDates ,
                missedTotalBetweenDates , rejectedTotalBetweenDates };

        Log.d("Database operation: ","Data sorted between dates"+totalCallsType[1]);

        cursorIncoming.close();
        cursorOutgoing.close();
        cursorMissed.close();
        cursorRejected.close();

        sqLiteDatabase.close();

        return totalCallsType;
    }

    public int[] getCallTypeTotalByNumber(String phnumber){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] argumentIn={phnumber, "INCOMING"};
        Cursor cursorIncoming = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE ("+ CallLogContract.CallLogEntry.PHONE_NUMBER + " = ? ) " +
                "AND "+ CallLogContract.CallLogEntry.CALL_TYPE+" =? ", argumentIn);

        String[] argumentOut={phnumber, "OUTGOING"};
        Cursor cursorOutgoing = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE ("+ CallLogContract.CallLogEntry.PHONE_NUMBER + " = ?  ) " +
                "AND "+ CallLogContract.CallLogEntry.CALL_TYPE+" =? ", argumentOut);

        String[] argumentMissed={phnumber, "MISSED"};
        Cursor cursorMissed = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE ("+ CallLogContract.CallLogEntry.PHONE_NUMBER + " = ? ) " +
                " AND "+ CallLogContract.CallLogEntry.CALL_TYPE+" =? ", argumentMissed);

        String[] argumentRejected={phnumber, "REJECTED"};
        Cursor cursorRejected = sqLiteDatabase.rawQuery("SELECT * FROM "+ CallLogContract.CallLogEntry.TABLE_NAME+
                " WHERE ("+ CallLogContract.CallLogEntry.PHONE_NUMBER + " = ? ) " +
                "AND "+ CallLogContract.CallLogEntry.CALL_TYPE+" =? ", argumentRejected);

        int incomingTotal = cursorIncoming.getCount();
        int outgoingTotal = cursorOutgoing.getCount();
        int missedTotal = cursorMissed.getCount();
        int rejectedTotal = cursorRejected.getCount();

        int[] totalCallsType = {incomingTotal , outgoingTotal , missedTotal , rejectedTotal };

        Log.d("Database operation: ","Data sorted between dates"+totalCallsType[1]);

        cursorIncoming.close();
        cursorOutgoing.close();
        cursorMissed.close();
        cursorRejected.close();

        sqLiteDatabase.close();

        return totalCallsType;
    }
}
