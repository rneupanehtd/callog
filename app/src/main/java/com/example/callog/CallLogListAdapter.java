package com.example.callog;

import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CallLogListAdapter extends ArrayAdapter<CallLog> {

    private final ArrayList<CallLog> callLogs;

    public CallLogListAdapter(@NonNull Context context, ArrayList<CallLog> callLogs) {
        super(context, R.layout.list_item_call_log);
        this.callLogs = callLogs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CallLogHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_call_log, null);
            holder = new CallLogHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CallLogHolder) convertView.getTag();
        }

        CallLog callLog = callLogs.get(position);
        holder.phNumberText.setText("Contact Number: "+callLog.getPhNumber());
        holder.callTypeText.setText("Call Type: "+callLog.getCallType());
        holder.callDurationText.setText("Call Duration in sec: "+callLog.getDuration());
        holder.callDateText.setText("Call Date/Time: "+callLog.getDate());

        return convertView;
    }

    @Override
    public int getCount() {
        return callLogs.size();
    }

    class CallLogHolder {

        private TextView phNumberText, callTypeText, callDurationText, callDateText;

        public CallLogHolder(View view) {
            phNumberText = view.findViewById(R.id.ph_number);
            callTypeText = view.findViewById(R.id.call_type);
            callDateText = view.findViewById(R.id.call_date);
            callDurationText = view.findViewById(R.id.call_duration);
        }
    }
}
