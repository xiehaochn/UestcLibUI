package com.hawx.uestclibui;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/2.
 */
public class SearchResultActivity extends AppCompatActivity {
    private final String RESULT_URL="RESULT_URL_KEY";
    private Toolbar mtoolBar;
    private String url_result;
    private String book_info;
    private List<String> ItemsEntrys=new ArrayList<String>();
    private List<String> InfoEntrys=new ArrayList<String>();
    private String[] matcher=new String[10];
    private int num;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1: {
                    book_info = msg.getData().getString("detail");
                    num=0;
                    Document doc = Jsoup.parse(book_info);
                    Elements itemsentrys = doc.getElementsByClass("bibItemsEntry");
                    for (Element itemsentry : itemsentrys) {
                        Elements tds = itemsentry.getElementsByTag("td");
                        num+=1;
                        for (Element td : tds) {
                            String td_string = td.text();
                            ItemsEntrys.add(td_string);
                            Log.d("bibItemsEntry", td_string);
                        }
                    }
                    Log.d("bibItemsEntry_num",""+num);
                    Elements labels = doc.getElementsByClass("bibInfoLabel");
                    for (Element label : labels) {
                        String label_string = label.text();
                        Log.d("bibInfoLabel", label_string);
                    }
                    Elements datas = doc.getElementsByClass("bibInfoData");
                    for (Element data :datas) {
                        String data_string =data.text();
                        Log.d("bibInfoData", data_string);
                    }
                    Elements infoentrys=doc.getElementsByClass("bibInfoEntry");
                    for(Element infoentry:infoentrys){
                        String infoentry_string=infoentry.text();
                        InfoEntrys.add(infoentry_string);
                        Log.d("bibInfoEntry",infoentry_string);
                    }
                    setTable();
                    matchingAndDivision(InfoEntrys);
                    break;
                }
                case 2:{
                    Toast.makeText(SearchResultActivity.this,"连接超时，请检查网络设置",Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };
    private TextView author;
    private TextView book_name;
    private TextView publisher;
    private TextView carrier;
    private TextView annotation;
    private TextView book_theme;
    private TextView otherone;
    private TextView extra_name;
    private TextView standard_num;
    private TextView call_num;
    private void matchingAndDivision(List<String> arrayList) {
        author= (TextView) findViewById(R.id.author);
        book_name= (TextView) findViewById(R.id.book_name);
        publisher= (TextView) findViewById(R.id.publisher);
        carrier= (TextView) findViewById(R.id.carrier);
        annotation= (TextView) findViewById(R.id.annotation);
        book_theme= (TextView) findViewById(R.id.book_theme);
        otherone= (TextView) findViewById(R.id.otherone);
        extra_name= (TextView) findViewById(R.id.extra_name);
        standard_num= (TextView) findViewById(R.id.standard_num);
        call_num= (TextView) findViewById(R.id.call_num);
        String author_regular = "("+matcher[0]+")"+"(.*)"+"("+matcher[1]+")";
        Pattern author_pattern = Pattern.compile(author_regular);
        Matcher author_m = author_pattern.matcher(arrayList.get(0));
        if (author_m.find( )) {
            author.setText(author_m.group(2));
        } else {
            System.out.println("NO MATCH");
        }
        String bookname_regular = "("+matcher[1]+")"+"(.*)"+"("+matcher[2]+")";
        Pattern bookname_pattern = Pattern.compile(bookname_regular);
        Matcher bookname_m = bookname_pattern.matcher(arrayList.get(0));
        if (bookname_m.find( )) {
           book_name.setText(bookname_m.group(2));
        } else {
            System.out.println("NO MATCH");
        }
        String publisher_regular = "("+matcher[2]+")"+"(.*)";
        Pattern publisher_pattern = Pattern.compile(publisher_regular);
        Matcher publisher_m = publisher_pattern.matcher(arrayList.get(0));
        if (publisher_m.find( )) {
            publisher.setText(publisher_m.group(2));
        } else {
            System.out.println("NO MATCH");
        }
        String carrier_regular = "("+matcher[3]+")"+"(.*)"+"("+matcher[4]+")";
        Pattern carrier_pattern = Pattern.compile(carrier_regular);
        Matcher carrier_m = carrier_pattern.matcher(arrayList.get(2));
        if (carrier_m.find( )) {
            carrier.setText(carrier_m.group(2));
        } else {
            System.out.println("NO MATCH");
        }
        String annotation_regular = "("+matcher[4]+")"+"(.*)"+"("+matcher[5]+")";
        Pattern annotation_pattern = Pattern.compile(annotation_regular);
        Matcher annotation_m= annotation_pattern.matcher(arrayList.get(2));
        if (annotation_m.find( )) {
            annotation.setText(annotation_m.group(2));
        } else {
            System.out.println("NO MATCH");
        }
        //============================================================================================================
        //   主题---------其他责任人-----附加题名---------标准号-----------索书号
        //     5--------------8--------------9---------------6---------------7
        //book_theme------otherone------extra_name------standard_num------call_num
        //
        //
        //               |exist-8|                 |exist-9|                   |exist-6|                   |branch-1 end|
        //        |------|finded|-----|8-9|-------|finded|------|9-6|----------|finded|---------|6-7|---------|finded|
        //        |      |set-5|            |     |set-8|              |        |set-9|                        |set-6|
        //        |                         |                          |
        //|5-8|---|                         |                          |    |not exist-6|                  |branch-2 end|
        //        |                         |                          |-----|else|-----------|9-7|-----------|finded|
        //        |                         |                                                                 |set-9|
        //        |                         |
        //        |                         |    |not exist-9|                 |exist-6|                     |branch-3 end|
        //        |                         |------|else|--------|8-6|----------|finded|--------|6-7|----------|finded|
        //        |                                                     |        |set-8|                        |set-6|
        //        |                                                     |
        //        |                                                     |     |not exist-6|                  |branch-4 end|
        //        |                                                     |-------|else|-----------|8-7|-----------|finded|
        //        |                                                                                              |set-8|
        //        |
        //        |   |not exist-8|                |exist-9|                      |exist-6|                    |branch-5 end|
        //        |-----|else|---------|5-9|--------|finded|------|9-6|-----------|finded|---------|6-7|-----------|finded|
        //                                   |      |set-5|              |        |set-9|                          |set-6|
        //                                   |                           |
        //                                   |                           |      |not exist-6|                   |branch-6 end|
        //                                   |                           |--------|else|------------|9-7|----------|finded|
        //                                   |                                                                      |set-9|
        //                                   |
        //                                   |      |not exist-9|                    |exist-6|                   |branch-7 end|
        //                                   |------|else|----------|5-6|------------|finded|---------|6-7|---------|finded|
        //                                                                  |        |set-5|                        |set-6|
        //                                                                  |
        //                                                                  |         |not exist-6|              |branch-8 end|
        //                                                                  |---------|else|-----------|5-7|---------|finded|
        //                                                                                                          |set-5|
        //====================================================================================================================================
        String booktheme_regular = "("+matcher[5]+")"+"(.*)"+"("+matcher[8]+")";//find 5-8
        Pattern booktheme_pattern = Pattern.compile(booktheme_regular);
        Matcher booktheme_m= booktheme_pattern.matcher(arrayList.get(2));
        if (booktheme_m.find( )) {
            //exist-8
            book_theme.setText(booktheme_m.group(2));//set-5
            String e1_r = "("+matcher[8]+")"+"(.*)"+"("+matcher[9]+")";//find 8-9
            Pattern e1_p = Pattern.compile(e1_r);
            Matcher e1_m= e1_p.matcher(arrayList.get(2));
            if(e1_m.find()){
                //exist-9
                otherone.setText(e1_m.group(2));//set-8
                String e2_r = "("+matcher[9]+")"+"(.*)"+"("+matcher[6]+")";//find 9-6
                Pattern e2_p = Pattern.compile(e2_r);
                Matcher e2_m= e2_p.matcher(arrayList.get(2));
                if(e2_m.find()){
                    //exist-6
                    extra_name.setText(e2_m.group(2));//set-9
                    String e3_r = "("+matcher[6]+")"+"(.*)"+"("+matcher[7]+")";//find 6-7
                    Pattern e3_p = Pattern.compile(e3_r);
                    Matcher e3_m= e3_p.matcher(arrayList.get(2));
                    if(e3_m.find()){
                        standard_num.setText(e3_m.group(2));//set-6,branch-1 end
                    }
                }else{
                    // not exist-6
                    String e4_r = "("+matcher[9]+")"+"(.*)"+"("+matcher[7]+")";//find 9-7
                    Pattern e4_p = Pattern.compile(e4_r);
                    Matcher e4_m= e4_p.matcher(arrayList.get(2));
                    if(e4_m.find()){
                        extra_name.setText(e4_m.group(2));//set-9,branch-2 end
                    }
                }
            }else{
                //not exist-9
                String e5_r = "("+matcher[8]+")"+"(.*)"+"("+matcher[6]+")";//find 8-6
                Pattern e5_p = Pattern.compile(e5_r);
                Matcher e5_m= e5_p.matcher(arrayList.get(2));
                if(e5_m.find()){
                    //exist-6
                    otherone.setText(e5_m.group(2));//set-8
                    String e6_r = "("+matcher[6]+")"+"(.*)"+"("+matcher[7]+")";//find 6-7
                    Pattern e6_p = Pattern.compile(e6_r);
                    Matcher e6_m= e6_p.matcher(arrayList.get(2));
                    if(e6_m.find()){
                        standard_num.setText(e6_m.group(2));//set-6,branch-3 end
                    }else{

                    }

                }else{
                    //not exist-6
                    String e7_r = "("+matcher[8]+")"+"(.*)"+"("+matcher[7]+")";//find 8-7
                    Pattern e7_p = Pattern.compile(e7_r);
                    Matcher e7_m= e7_p.matcher(arrayList.get(2));
                    if(e7_m.find()){
                        otherone.setText(e7_m.group(2));//set-8,branch-4 end
                    }
                }
            }
        } else {
            //not exist-8
            String e9_r = "("+matcher[5]+")"+"(.*)"+"("+matcher[9]+")";//find 5-9
            Pattern e9_p = Pattern.compile(e9_r);
            Matcher e9_m= e9_p.matcher(arrayList.get(2));
            if (e9_m.find( )) {
                //exist-9
                book_theme.setText(e9_m.group(2));//set-5
                String e10_r = "("+matcher[9]+")"+"(.*)"+"("+matcher[6]+")";//find 9-6
                Pattern e10_p = Pattern.compile(e10_r);
                Matcher e10_m= e10_p.matcher(arrayList.get(2));
                if(e10_m.find()){
                    //exist-6
                    extra_name.setText(e10_m.group(2));//set-9
                    String e11_r = "("+matcher[6]+")"+"(.*)"+"("+matcher[7]+")";//find 6-7
                    Pattern e11_p = Pattern.compile(e11_r);
                    Matcher e11_m= e11_p.matcher(arrayList.get(2));
                    if(e11_m.find()){
                        standard_num.setText(e11_m.group(2));//set-6,branch-5,end
                    }
                }else{
                    //not exist-6
                    String e12_r = "("+matcher[9]+")"+"(.*)"+"("+matcher[7]+")";//find 9-7
                    Pattern e12_p = Pattern.compile(e12_r);
                    Matcher e12_m= e12_p.matcher(arrayList.get(2));
                    if(e12_m.find()){
                        extra_name.setText(e12_m.group(2));//set-9,branch-6 end
                    }
                }
            } else {
                //not exist-9
                String e13_r = "("+matcher[5]+")"+"(.*)"+"("+matcher[6]+")";//find 5-6
                Pattern e13_p = Pattern.compile(e13_r);
                Matcher e13_m= e13_p.matcher(arrayList.get(2));
                if(e13_m.find()){
                    //exist-6
                    book_theme.setText(e13_m.group(2));//set-5
                    String e14_r = "("+matcher[6]+")"+"(.*)"+"("+matcher[7]+")";//find 6-7
                    Pattern e14_p = Pattern.compile(e14_r);
                    Matcher e14_m= e14_p.matcher(arrayList.get(2));
                    if(e14_m.find()){
                        standard_num.setText(e14_m.group(2));//set-6,branch-7 end
                    }
                }else{
                    //not exist-6
                    String e15_r = "("+matcher[5]+")"+"(.*)"+"("+matcher[7]+")";//find 5-7
                    Pattern e15_p = Pattern.compile(e15_r);
                    Matcher e15_m= e15_p.matcher(arrayList.get(2));
                    if(e15_m.find()){
                        book_theme.setText(e15_m.group(2));//set-5,branch-8 end
                    }
                }
            }
        }
        //=======================================================================================================================
        String callnum_regular = "("+matcher[7]+")"+"(.*)";
        Pattern callnum_pattern = Pattern.compile(callnum_regular);
        Matcher callnum_m= callnum_pattern.matcher(arrayList.get(2));
        if (callnum_m.find( )) {
            call_num.setText(callnum_m.group(2));
        } else {
            System.out.println("NO MATCH");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_result, menu);
        return true;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_layout);
        Bundle bundle=this.getIntent().getExtras();
        url_result=bundle.getString(RESULT_URL);
        mtoolBar= (Toolbar) findViewById(R.id.toolbar);
        mtoolBar.setTitle(R.string.app_name);
        mtoolBar.setSubtitle(R.string.detail);
        mtoolBar.setTitleTextAppearance(this,R.style.titleTextStyle);
        mtoolBar.setSubtitleTextAppearance(this,R.style.subtitleTextStyle);
        setSupportActionBar(mtoolBar);
        mtoolBar.setOnMenuItemClickListener(onMenuItemClick);
        mtoolBar.setNavigationIcon(R.drawable.ic_back);
        mtoolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        intiMatcher();
        getInfomation();

    }
    private void setTable() {
        final LinearLayout table_container= (LinearLayout) findViewById(R.id.table_container);
        TableLayout tl =new TableLayout(this);
        for(int i=0;i<num;i++){
            tl=generateTableView(i);
            table_container.addView(tl);
        }

    }

    private TableLayout generateTableView(int i) {
        TableLayout tl_r=new TableLayout(this,ItemsEntrys.get(i*5),ItemsEntrys.get(i*5+1),ItemsEntrys.get(i*5+2),ItemsEntrys.get(i*5+3),ItemsEntrys.get(i*5+4));
        return tl_r;
    }

    private void intiMatcher() {
        matcher[0]="主要责任者";
        matcher[1]="题名";
        matcher[2]="出版者";
        matcher[3]="载体形态";
        matcher[4]="附注";
        matcher[5]="主题";
        matcher[6]="标准号";
        matcher[7]="索书号";
        matcher[8]="其他责任人";
        matcher[9]="附加题名";
    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(android.view.MenuItem item) {
            switch (item.getItemId()) {
                case R.id.collection: {
                    Toast.makeText(SearchResultActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
            return true;
        }
    };



    private void getInfomation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpURLConnection con = null;
                try { URL url_URL = new URL(url_result);
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
                    bundle.putString("detail", response);
                    msg.setData(bundle);
                    mhandler.sendMessage(msg);
                } catch (SocketTimeoutException e) {
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
