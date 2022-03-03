package com.example.callog;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SearchByNumberActivity extends AppCompatActivity {
    private TextView tvPhNumber;
    private Button btnSearch;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_number);

        tvPhNumber = (TextView)findViewById(R.id.tvPhHint);
        btnSearch = (Button)findViewById(R.id.btnSearchByNumber);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phNumber = String.valueOf(tvPhNumber.getText());
                setUpLineChart(phNumber);
            }
        });
    }

    public void setUpLineChart(String phNumber){
        CallLogDBHelper callLogDBHelper = new CallLogDBHelper(SearchByNumberActivity.this);
        int[] totalCallsByNumber = callLogDBHelper.getCallTypeTotalByNumber(phNumber);

        lineChart = (LineChart)findViewById(R.id.lineChart);

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i =0; i<totalCallsByNumber.length; i++) {
            entries.add(new Entry(i, totalCallsByNumber[i]));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setCircleColors(ColorTemplate.COLORFUL_COLORS);
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setCircleRadius(8f);
        lineDataSet.setCircleHoleRadius(4f);
        lineDataSet.setCircleHoleColor(Color.BLACK);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] callTypes = {"Incoming", "Outgoing", "Missed", "Rejected"};
        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return callTypes[(int) value];
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(valueFormatter);
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setGranularity(1f);
        YAxis yAxisLeft = lineChart.getAxisRight();
        yAxisLeft.setGranularity(1f);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.animateY(2000);
        lineChart.setDrawGridBackground(true);
        lineChart.getDescription().setText("Line chart for the selected number");
        lineChart.getDescription().setTextSize(10f);
        lineChart.getLegend().setEnabled(false);

        lineChart.invalidate();


    }
}
