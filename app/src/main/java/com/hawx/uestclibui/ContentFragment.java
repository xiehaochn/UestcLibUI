package com.hawx.uestclibui;


import android.content.Context;
import android.content.Intent;
import android.net.http.HttpsConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Administrator on 2015/11/28.
 */
public class ContentFragment extends Fragment {
    private final static String KEY_TITLE="key_title";
    private View view;
    private String user_data;
    private String url_request;
    private String header_location;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:{
                    user_data=msg.getData().getString("user_data");
                    Log.d("user_data",user_data);
                    startLoginActivity(getContext(),user_data);
                    break;
                }
                case 2:{
                    Toast.makeText(getContext(),"连接超时，请检查网络设置",Toast.LENGTH_SHORT).show();
                    break;
                }
                case 3:{
                    Toast.makeText(getContext(),"帐号信息有误",Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };
    private final String USER_DATA="USER_DATA";

    public void startLoginActivity(Context context, String data){
        Bundle bundle=new Bundle();
        bundle.putString(USER_DATA,data);
        Intent intent=new Intent(context,LoginActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public static ContentFragment newInstance(String title) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String title = (String) getArguments().get(KEY_TITLE);
        if(title.equals("书籍检索")) {
            view = inflater.inflate(R.layout.home_page, null);
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getActivity().getSupportFragmentManager()
                    , FragmentPagerItems.with(getActivity())
                    .add(R.string.tab_title1, SearchFragment.class)
                    .add(R.string.tab_title2, SearchFragmentEx.class)
                    .create());
            ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            viewPager.setAdapter(adapter);
            SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.tab);
            viewPagerTab.setViewPager(viewPager);
        }else if(title.equals("个人中心")){
            view=inflater.inflate(R.layout.user_center_page,null);
            final EditText editText_id= (EditText) view.findViewById(R.id.extpatid);
            final EditText editText_pw= (EditText) view.findViewById(R.id.extpatpw);
            Button login= (Button) view.findViewById(R.id.login);
            Button back_home= (Button) view.findViewById(R.id.back_home);
            back_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                    LeftMenuFragment leftMenuFragment= (LeftMenuFragment) fragmentManager.findFragmentByTag("LeftMenuFragment");
                    leftMenuFragment.onListItemClick(leftMenuFragment.getListView(),leftMenuFragment.getView(),0,0);

                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user_id=editText_id.getText().toString();
                    String user_pw=editText_pw.getText().toString();
                    if(user_id.equals("")|user_pw.equals("")){
                        Toast.makeText(getContext(),"请输入帐号信息",Toast.LENGTH_SHORT).show();
                    }else{
                        url_request="https://webpac.uestc.edu.cn/patroninfo*chx?extpatid="+user_id+"&extpatpw="+user_pw+"&submit=submit";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpsURLConnection con1 = null;
                                HttpsURLConnection con=null;
                                try {
                                    URL url_URL = new URL(url_request);
                                    con1 = (HttpsURLConnection) url_URL.openConnection();
                                    con1.setInstanceFollowRedirects(false);
                                    con1.setRequestMethod("GET");
                                    con1.setConnectTimeout(8000);
                                    con1.setReadTimeout(8000);
                                    String COOKIES_HEADER = "Set-Cookie";
                                    CookieManager msCookieManager = new CookieManager();
                                    Map<String, List<String>> headerFields = con1.getHeaderFields();
                                    List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                                    if(cookiesHeader != null)
                                    {
                                        for (String cookie : cookiesHeader)
                                        {
                                            msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                                        }
                                    }
                                    header_location=con1.getHeaderField("Location");
                                    if(header_location!=null){
                                    Log.d("Location",header_location);
                                    String url_detail="https://webpac.uestc.edu.cn"+header_location;
                                    Log.d("URL_USER_DATA",url_detail);
                                    //===================================================================================
                                    URL url_URL_detail=new URL(url_detail);
                                    con = (HttpsURLConnection) url_URL_detail.openConnection();
                                    con.setRequestMethod("GET");
                                    if(msCookieManager.getCookieStore().getCookies().size() > 0)
                                    {
                                        //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                                        con.setRequestProperty("Cookie",
                                                TextUtils.join(";",  msCookieManager.getCookieStore().getCookies()));
                                    }
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
                                    Message msg = new Message();
                                    msg.what = 1;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("user_data", response);
                                    msg.setData(bundle);
                                    mhandler.sendMessage(msg);}else {
                                        mhandler.sendEmptyMessage(3);
                                    }

                                } catch (SocketTimeoutException e) {
                                    // TODO Auto-generated catch block
                                    mhandler.sendEmptyMessage(2);
                                    e.printStackTrace();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            });
        }else if(title.equals("关于")){
            view=inflater.inflate(R.layout.about_us,null);
        }
        else{
            TextView tv = new TextView(getActivity());
            if (!TextUtils.isEmpty(title))
            {
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(40);
                tv.setText(title);
            }
            view=tv;

        }

        return view;
    }
}
