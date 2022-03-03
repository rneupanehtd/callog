package com.example.callog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchByDateActivity extends AppCompatActivity {

    private TextView tvStartDate;
    private TextView tvEndDate;
    private Button btnSearch;

    private BarChart barChart;

    private DatePickerDialog.OnDateSetListener startDateSetListener;
    private DatePickerDialog.OnDateSetListener endDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_date);

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnSearch = findViewById(R.id.searchbtn);

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog startDateDialogue = new DatePickerDialog(SearchByDateActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        startDateSetListener,
                        year, month, day);
                startDateDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                startDateDialogue.show();
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog endDateDialogue = new DatePickerDialog(SearchByDateActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        endDateSetListener,
                        year, month, day);
                endDateDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                endDateDialogue.show();
            }
        });

        startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                String date = dayOfMonth+"-"+month+"-"+year;
                tvStartDate.setText(date);
            }
        };

        endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                String date = dayOfMonth+"-"+month+"-"+year;
                tvEndDate.setText(date);
            }
        };

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get input dates after search button is clicked
                String startDate = (String) tvStartDate.getText();
                String endDate = (String) tvEndDate.getText();
                String startDateTime = startDate;
                String endDateTime = endDate;
                setUpBarChart(startDateTime,endDateTime);
            }
        });
    }

    public void setUpBarChart(String startDateString, String endDateString){

        CallLogDBHelper callLogDBHelper = new CallLogDBHelper(SearchByDateActivity.this);
        int[] totalCallsBetweenDates = callLogDBHelper.getCallTypeTotalBetweenDates(startDateString, endDateString);

        barChart = (BarChart)findViewById(R.id.barChart);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for(int i=0; i<totalCallsBetweenDates.length; i++){
            barEntries.add(new BarEntry(i, totalCallsBetweenDates[i]));
        }

        Log.d("Bar chart:","Bar chart entries created");

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextSize(12f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        String[] callTypes = {"Incoming", "Outgoing", "Missed", "Rejected"};
        IndexAxisValueFormatter indexAxisValueFormatter = new IndexAxisValueFormatter(callTypes);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(indexAxisValueFormatter);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.8f);

        barChart.setData(barData);
        Log.d("Bar chart:", "Bar chart shown");

        barChart.animateY(1000);
        barChart.invalidate();
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);
        barChart.setTouchEnabled(false);
        barChart.setFitBars(true);
        barChart.getDescription().setText("Bar chart for the selected period");
        barChart.getDescription().setTextSize(10f);
        barChart.getLegend().setEnabled(false);
    }

}
