package com.example.firstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends ArrayAdapter {

    //使用自定义方式构建Adapter
    private static final String TAG = "MyAdapter";

    public MyAdapter(@NonNull Context context, int resource, ArrayList<HashMap<String,String>>list) {
        super(context, resource,list);
    }

    public View getView(int position, View converView, ViewGroup parent){
        View itemView = converView;
        if (itemView==null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_my_list2,parent,false);
        }
        Map<String,String> map = (Map<String, String>)getItem(position);
        TextView title = (TextView)itemView.findViewById(R.id.itemTitle);
        TextView detail = (TextView)itemView.findViewById(R.id.itemDetail);

        title.setText("Title:"+map.get("ItemTitle"));
        detail.setText("Detail:"+map.get("ItemDetail"));

        return itemView;
    }
}
