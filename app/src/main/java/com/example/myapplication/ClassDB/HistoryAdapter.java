package com.example.myapplication.ClassDB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<String> {

    public HistoryAdapter(Context context, ArrayList<String> transactions) {
        super(context, 0, transactions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String transaction = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView transactionTextView = convertView.findViewById(android.R.id.text1);
        transactionTextView.setText(transaction);

        return convertView;
    }
}
