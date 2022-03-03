package com.example.callog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnViewAllLog;
    private Button btnSearchByDate;
    private Button btnSearchByNumber;
    CallLogDBHelper callLogDBHelper;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create callLogDBHelper object
        callLogDBHelper = new CallLogDBHelper(this);
        //call accessCallLogs(view) method to ask for user permission to access call logs
        accessCallLogs(view);
        //call method to displa pie chart for call log summary
        setUpPieChart();
        //method to display horizontal linear layout to show call summary
        displayCallSummary();

        //view all log button set on action
        btnViewAllLog = findViewById(R.id.ButtonViewAllLogs);
        btnViewAllLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewAllLogsActivity.class);
                startActivity(intent);
            }
        });

        //search by date button set on action
        btnSearchByDate = findViewById(R.id.searchByDateButton);
        btnSearchByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchByDateActivity.class);
                startActivity(intent);
            }
        });

        //search by number button set on action
        btnSearchByNumber = findViewById(R.id.searchByNumberButton);
        btnSearchByNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchByNumberActivity.class);
                startActivity(intent);
            }
        });

    }

    @SuppressLint("NewApi")
    public void accessCallLogs(View view){
        //check if call log permission is already available
        //if call log permission is already granted, access the call logs.
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CALL_LOG)== PackageManager.PERMISSION_GRANTED){
            //do stuff
            //all call logs to the callLogDBHelper object
            callLogDBHelper.addAllLogs();
            //method to display piechart
            setUpPieChart();
            //method to display horizontal linear layout to show call summary
            displayCallSummary();
        }
        else{
            //call log permission has not been granted
            //provide an additional rationale to the user if permission was not granted
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG)){
                Toast.makeText(this,"", Toast.LENGTH_SHORT).show();
            }
            //request call log permission
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode==1){
            //receive permission result for call log
            //check if the only required permission has been granted
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //call log permission is granted
                Toast.makeText(this,"Permission granted!", Toast.LENGTH_SHORT).show();
                //do stuff
                //all call logs to the callLogDBHelper object
                callLogDBHelper.addAllLogs();
                //method to display piechart
                setUpPieChart();
                //method to display horizontal linear layout to show call summary
                displayCallSummary();
//                ArrayList<String> callLogData = callLogs.readCallLogsData();
//                Log.d("MainActivity", callLogData.toString());
            }
            else{
                Toast.makeText(this,"Permission denied!", Toast.LENGTH_SHORT).show();
                //close the app when permission is denied
                closeApp();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //method to close app if permission is not granted
    public void closeApp(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            finishAffinity();
        }
        else{
            finish();
        }
    }

    private void setUpPieChart(){
        //create callLogDBHelper object
        CallLogDBHelper callLogDBHelper = new CallLogDBHelper(MainActivity.this);
        //create array of integers to store each call type totals
        int totalCalls[] = callLogDBHelper.getCallTypeTotal();
        Log.d("MainActivity:", String.valueOf(totalCalls.length));
        //create array of string showing different call types
        String callTypes[] = {"Incoming", "Outgoing", "Missed", "Rejected"};

        //populate the list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i=0 ; i<totalCalls.length; i++){
            pieEntries.add(new PieEntry(totalCalls[i], callTypes[i]));
        }
        //set data set for pie chart and its design
        PieDataSet pieDataSet = new PieDataSet (pieEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //set pie chart data set into pie data
        PieData pieData = new PieData(pieDataSet);

        //set piedata into piechart
        PieChart pieChartForAllLogs = (PieChart) findViewById(R.id.PieChartForTotalLogs);
        pieChartForAllLogs.setData(pieData);

        //access piechart properties
        pieChartForAllLogs.animateY(1000);
        pieChartForAllLogs.getDescription().setEnabled(false);
        pieData.setValueFormatter(new PercentFormatter(pieChartForAllLogs));
        pieData.setValueTextSize(15);
        pieData.setValueTextColor(R.color.colorBlue);
        pieChartForAllLogs.setUsePercentValues(true);
        pieChartForAllLogs.setDrawEntryLabels(false);

        //create legend for the pie chart
        Legend legend = pieChartForAllLogs.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChartForAllLogs.invalidate();
    }

    private void displayCallSummary(){
        //create callLogDBHelper object
        CallLogDBHelper callLogDBHelper = new CallLogDBHelper(MainActivity.this);
        //create array of integers to store each call type totals
        int totalCalls[] = callLogDBHelper.getCallTypeTotal();
        int incomingTotal = totalCalls[0];
        int outgoingTotal = totalCalls[1];
        int missedTotal = totalCalls[2];
        int rejectedTotal = totalCalls[3];

        //display call summary record in the textview
        TextView tvIncoming = (TextView)findViewById(R.id.tvShowIncoming);
        tvIncoming.setText("\nINCOMING\n"+incomingTotal);
        TextView tvOutgoing = (TextView)findViewById(R.id.tvShowOutgoing);
        tvOutgoing.setText("\nOUTGOING\n"+outgoingTotal);
        TextView tvMissed = (TextView)findViewById(R.id.tvShowMissed);
        tvMissed.setText("\nMISSED\n"+missedTotal);
        TextView tvRejected = (TextView)findViewById(R.id.tvShowRejected);
        tvRejected.setText("\nREJECTED\n"+rejectedTotal);
    }

}
