package com.hawx.uestclibui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/12/4.
 */
public class TableLayoutDate extends LinearLayout{
    public TableLayoutDate(Context context){
        super(context);

    }
    public TableLayoutDate(Context context,String name,String num,String date,String call_num){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.table_layout_date,this);
        TextView tv1= (TextView) findViewById(R.id.name);
        TextView tv2= (TextView) findViewById(R.id.num);
        TextView tv3= (TextView) findViewById(R.id.state);
        TextView tv4= (TextView) findViewById(R.id.call_num);
        tv1.setText(name);
        tv2.setText(num);
        tv3.setText(date);
        tv4.setText(call_num);
    }

}
