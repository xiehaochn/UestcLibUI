package com.hawx.uestclibui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/3.
 */
public class TableLayout extends LinearLayout {
    public TableLayout(Context context){
        super(context);

    }
    private String local_string;
    private TextView tv6;
    private String local_final_1;
    private String local_final_2;
    private String local_url="http://10.21.16.217/RFIDWeb/TSDW/GotoFlash.aspx?szBarCode=";
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:{
                    local_string=msg.getData().getString("local_detail");
                    Log.d("LOCAL_HTML",local_string);
                    String regular = "("+"strWZxxxxxx"+")"+"(.*)"+"("+"层"+")";
                    Pattern pattern = Pattern.compile(regular);
                    Matcher loc= pattern.matcher(local_string);
                    if (loc.find( )) {
                        local_final_1=loc.group(2)+"层";
                        Log.d("1",local_final_1);
                    }
                    if(local_final_1!=null){
                    String regular2= "(\\[)(.*)";
                    Pattern pattern2=Pattern.compile(regular2);
                    Matcher matcher2=pattern2.matcher(local_final_1);
                    if(matcher2.find()){
                        local_final_2="["+matcher2.group(2);
                        tv6.setText(local_final_2);
                    }}else{
                        Toast.makeText(getContext(),"没有相关信息",Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case 2:{
                    Toast.makeText(getContext(),"请求超时，请检测网络设置",Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };

    public TableLayout(Context context, String local_lib, String num, String state, final String local_num, String remark) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.table_layout,this);
        TextView tv1= (TextView) findViewById(R.id.local_lib);
        TextView tv2= (TextView) findViewById(R.id.num);
        TextView tv3= (TextView) findViewById(R.id.state);
        TextView tv4= (TextView) findViewById(R.id.local_num);
        TextView tv5= (TextView) findViewById(R.id.remark);
        tv6= (TextView) findViewById(R.id.local_detail);
        Button bt= (Button) findViewById(R.id.search_local_detail);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    HttpURLConnection con = null;
                    @Override
                    public void run() {
                        try {
                            String regular="\\s*|\t|\r|\n";
                            Pattern pattern=Pattern.compile(regular);
                            Matcher matcher=pattern.matcher(local_num);
                            String url =local_url+matcher.replaceAll("");
                            Log.d("LOCAL_URL",""+url);
                            URL url_URL = new URL(url);
                            con = (HttpURLConnection) url_URL.openConnection();
                            con.setRequestMethod("GET");
                            con.setConnectTimeout(8000);
                            con.setReadTimeout(8000);
                            InputStream in = con.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"GB2312"));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            String response = builder.toString();
                            Message msg = new Message();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("local_detail", response);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        } catch (SocketTimeoutException e) {
                            // TODO Auto-generated catch block
                            mHandler.sendEmptyMessage(2);
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        finally {
                            try {
                                con.disconnect();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }}
                }).start();
            }
        });
        tv1.setText(local_lib);
        tv2.setText(num);
        tv3.setText(state);
        tv4.setText(local_num);
        tv5.setText(remark);
    }


}
