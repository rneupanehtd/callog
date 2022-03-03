package com.example.callog;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewAllLogsActivity extends AppCompatActivity {
    private static final String TAG = "ListDataActivity";
    CallLogDBHelper callLogDBHelper;
    private ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_logs);

        myListView = findViewById(R.id.listViewAllLogs);
        callLogDBHelper = new CallLogDBHelper(this);
        textViewReturn();
    }

    public void textViewReturn() {
        Log.d(TAG, "textViewReturn: Displaying data in the listview");

        //store cursor from getAllLogs() method of CallLogDBHelper into cursor result
        Cursor result = callLogDBHelper.getAllLogs();

        //create arraylist to add data
        ArrayList<CallLog> callLogs = new ArrayList<>();

        //extract data in arraylist
        while (result.moveToNext()) {
            Log.d(TAG, "textViewReturn: ");
            String phNum = result.getString(result.getColumnIndex(CallLogContract.CallLogEntry.PHONE_NUMBER));
            String calltype = result.getString(result.getColumnIndex(CallLogContract.CallLogEntry.CALL_TYPE));
            String callDuration = result.getString(result.getColumnIndex(CallLogContract.CallLogEntry.CALL_DURATION));
            String callDate = result.getString(result.getColumnIndex(CallLogContract.CallLogEntry.CALL_DATE));

            CallLog callLog = new CallLog(phNum, calltype, callDate, callDuration);
            callLogs.add(callLog);
        }

        Log.d(TAG, "textViewReturn: " + callLogs.size());

        //create listadapter to display the all call logs
        ListAdapter adapter = new CallLogListAdapter(this, callLogs);
        myListView.setAdapter(adapter);

        //close the cursor
        result.close();
    }
}
