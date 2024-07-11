package com.fertail.istock.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fertail.istock.R;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<String> items;

    public CustomSpinnerAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, parent, false);
        TextView textView = view.findViewById(R.id.text_view);
        textView.setText(items.get(position));
        return view;
    }
}