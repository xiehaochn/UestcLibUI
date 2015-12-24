package com.hawx.uestclibui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/4.
 */
public class LoginActivity extends AppCompatActivity {
    private final String USER_DATA="USER_DATA";
    private String data;
    private Toolbar mtoolBar;
    private List<String> BookDetail=new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Bundle bundle=this.getIntent().getExtras();
        data=bundle.getString(USER_DATA);
        mtoolBar= (Toolbar) findViewById(R.id.toolbar);
        mtoolBar.setTitle(R.string.app_name);
        mtoolBar.setSubtitle(R.string.user_data);
        mtoolBar.setTitleTextAppearance(this,R.style.titleTextStyle);
        mtoolBar.setSubtitleTextAppearance(this,R.style.subtitleTextStyle);
        setSupportActionBar(mtoolBar);
        mtoolBar.setNavigationIcon(R.drawable.ic_back);
        mtoolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        parseData();
    }
    private String user_name;
    private TextView tv_name;
    private TextView tv_school;
    private TextView tv_date;
    private int num;

    private void parseData() {
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_school= (TextView) findViewById(R.id.tv_school);
        tv_date= (TextView) findViewById(R.id.tv_date);
        Document doc = Jsoup.parse(data);
        Elements user =doc.getElementsByClass("patNameAddress");
        for (Element user_one :user ) {
            Elements name=user_one.getElementsByTag("strong");
            for (Element name_one :name ) {
                user_name=name_one.text();
            }
        }
        tv_name.setText(user_name);
        String user_string=user.text();
        String school_regular = "("+user_name+")"+"(.*)(证件)";
        Pattern school_pattern = Pattern.compile(school_regular);
        Matcher school_m = school_pattern.matcher(user_string);
        if (school_m.find( )) {
            tv_school.setText(school_m.group(2));
        } else {
            tv_school.setText("没有相关信息");
        }
        String date_regular = "(证件)(.*)(续借)";
        Pattern date_pattern = Pattern.compile(date_regular);
        Matcher date_m = date_pattern.matcher(user_string);
        if (date_m.find( )) {
            tv_date.setText(date_m.group(2));
        } else {
            tv_date.setText("没有相关信息");
        }
        Elements book =doc.getElementsByClass("patFuncEntry");
        num=0;
        for (Element book_one :book ) {
            num+=1;
            Elements book_detail=book_one.getElementsByTag("td");
            for (Element book_final:book_detail ) {
                String test=book_final.text();
                Log.d("test",test);
                if(test.equals("")){

                } else {
                    BookDetail.add(test);
                }
            }
        }
        setTable();
    }
    private void setTable() {
        final LinearLayout table_container= (LinearLayout) findViewById(R.id.date_container);
        TableLayoutDate tl =new TableLayoutDate(this);
        Log.d("NUM",""+num);
        for(int i=0;i<num;i++){
            tl=generateTableView(i);
            table_container.addView(tl);
        }

    }

    private TableLayoutDate generateTableView(int i) {
        TableLayoutDate tl_r=new TableLayoutDate(this,BookDetail.get(i*4),BookDetail.get(i*4+1),BookDetail.get(i*4+2),BookDetail.get(i*4+3));
        return tl_r;
    }

}
