package com.hawx.uestclibui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Administrator on 2015/11/28.
 */
public class SearchFragmentEx extends Fragment {
    private View view;
    private final String url_1="http://webpac.uestc.edu.cn/search~S1*chx/?searchtype=";
    private final String url_2="&searcharg=";
    private final String url_3="http://webpac.uestc.edu.cn";
    private final String RESULT_URL="RESULT_URL_KEY";
    private String search_content;
    private String search_type;
    private String url_next;
    private String url_all;
    private String respHtml;
    private String web;
    private EditText editText;
    private ListView result_listView;
    private List<String> result_string=new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private List<String> result_link=new ArrayList<String>();
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1 :{
                    result_string.clear();
                    result_link.clear();
                    adapter.clear();
                    web=msg.getData().getString("web");
                    Document doc = Jsoup.parse(web);
                    String title1 = doc.title();
                    Log.d("1","HTML Title is "+title1);
                    Elements titles =doc.getElementsByClass("briefcitTitle");
                    for (Element title : titles) {
                        Elements links = title.getElementsByTag("a");
                        for (Element link : links) {
                            String linkHref = link.attr("href");
                            Log.d("1","Link is "+linkHref);
                            result_link.add(linkHref);
                        }
                        String mtext = title.text();
                        Log.d("1","Search Result is "+mtext );
                        result_string.add(mtext);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                }
                case 2:{
                    Toast.makeText(getActivity(),"连接超时，请检查网络设置",Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        view =inflater.inflate(R.layout.search_fragment,null);
        RadioGroup radioGroup= (RadioGroup) view.findViewById(R.id.rg);
        radioGroup.check(R.id.rb1);
        editText= (EditText) view.findViewById(R.id.search_et);
        Button button= (Button) view.findViewById(R.id.search_bt);
        result_listView= (ListView) view.findViewById(R.id.search_list);
        adapter=new  ArrayAdapter<String>(getActivity(),R.layout.bookbrief_listlayout,result_string);
        result_listView.setAdapter(adapter);
        result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent,View view,int positiong,long id) {
                url_next=url_3+result_link.get(positiong);
                startResultActivity(getActivity(),url_next);
            }
        });
        search_type="X";
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1:{
                        search_type="X";
                        break;
                    }
                    case R.id.rb2:{
                        search_type="a";
                        Log.d("1","SearchType is "+search_type);
                        break;
                    }
                    case R.id.rb3:{
                        search_type="t";
                        Log.d("1","SearchType is "+search_type);
                        break;
                    }
                    case R.id.rb4:{
                        search_type="b";
                        Log.d("1","SearchType is "+search_type);
                        break;
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_content=editText.getText().toString();
                if(search_content.equals("")){
                    Toast.makeText(getActivity(),"请输入搜索内容",Toast.LENGTH_SHORT).show();
                }else {
                    String url_content_utf_8= null;
                    try {
                        url_content_utf_8 = URLEncoder.encode(search_content,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d("1",url_content_utf_8);
                    url_all = url_1 + search_type + url_2 +url_content_utf_8;
                    Log.d("1","URL is "+url_all);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //CloseableHttpClient httpclient = HttpClients.createDefault();
                            HttpURLConnection con = null;
                            try {
                                // HttpGet httpget = new HttpGet(url_all);
                                //CloseableHttpResponse response = httpclient.execute(httpget);
                                // StringBuffer strBuf = new StringBuffer();
                                //HttpEntity httpEnt = response.getEntity();
                                //String content = EntityUtils.toString(httpEnt, "UTF-8");
                                // InputStream inputStream = httpEnt.getContent();
                                // BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                                //String singleLine = "";
                                // while ((singleLine = br.readLine()) != null) {
                                //  strBuf.append(singleLine);
                                // }
                                //respHtml = strBuf.toString();
                                URL url_URL = new URL(url_all);
                                con = (HttpURLConnection) url_URL.openConnection();
                                con.setRequestMethod("GET");
                                con.setConnectTimeout(8000);
                                con.setReadTimeout(8000);
                                InputStream in = con.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                StringBuilder builder = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    builder.append(line);
                                }
                                String response = builder.toString();
                                Log.d("1", response);
                                Message msg = new Message();
                                msg.what = 1;
                                Bundle bundle = new Bundle();
                                bundle.putString("web", response);
                                msg.setData(bundle);
                                mhandler.sendMessage(msg);
                            } catch (SocketTimeoutException e) {
                                // TODO Auto-generated catch block
                                mhandler.sendEmptyMessage(2);
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
                            }
                        }
                    }).start();
                }
            }
        });

        return view;
    }
    public void startResultActivity(Context context, String url){
        String result_url=url;
        Bundle bundle=new Bundle();
        bundle.putString(RESULT_URL,url);
        Intent intent=new Intent(context,SearchResultActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
