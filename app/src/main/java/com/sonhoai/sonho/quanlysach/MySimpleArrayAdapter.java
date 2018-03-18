package com.sonhoai.sonho.quanlysach;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sonho on 3/18/2018.
 */

public class MySimpleArrayAdapter extends ArrayAdapter<InfoData> {

    private Activity context;
    private int layout;
    private List<InfoData> list;
    public MySimpleArrayAdapter(Context context, int textViewResourceId,
                                List<InfoData> objects) {
        super(context, textViewResourceId, objects);
        this.context=(Activity) context;
        this.layout=textViewResourceId;
        this.list=objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater flater=context.getLayoutInflater();
        View row=flater.inflate(layout, parent,false);
        TextView txt1=(TextView) row.findViewById(R.id.textView1);
        TextView txt2=(TextView) row.findViewById(R.id.textView2);
        TextView txt3=(TextView) row.findViewById(R.id.textView3);
        InfoData data=list.get(position);
        txt1.setText(data.getField1()==null?"":data.getField1().toString());
        txt2.setText(data.getField2()==null?"":data.getField2().toString());
        txt3.setText(data.getField3()==null?"":data.getField3().toString());
        if(position==0)
        {
            row.setBackgroundColor(Color.RED);
        }
        return row;
    }
}
