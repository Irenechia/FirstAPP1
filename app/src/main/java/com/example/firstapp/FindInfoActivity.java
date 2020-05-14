package com.example.firstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FindInfoActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {

    public final  String TAG = "FindInfoActivity";
    Handler handler;
    List<String> liList = new ArrayList<String>();
    List<String> herfList = new ArrayList<String>();
    List<String> resultList = new ArrayList<String>();
    private String updateDate = "";
    private String todayDate="";
    private int page = 0;
//    ListView listView = (ListView) findViewById(R.id.list_result);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_info);

        //获取数据和日期
        SharedPreferences spLi = getSharedPreferences("liList", MODE_PRIVATE);
        int liSize = spLi.getInt("liList", 0);
        for (int i = 0; i < liSize; i++) {
            String liString = spLi.getString("li_"+i, null);
            liList.add(liString);
        }

        updateDate = spLi.getString("update_date:","");

        SharedPreferences spHerf = getSharedPreferences("liList", MODE_PRIVATE);
        int herfSize = spHerf.getInt("liList", 0);
        for (int i = 0; i < herfSize; i++) {
            String herfString = spHerf.getString("herf_"+i, null);
            liList.add(herfString);
        }

        //获取当前时间
        final Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        todayDate = sdf1.format(today);


        //判断是否在同一周
        if(!isSameWeek(todayDate,updateDate)){
            //开启线程
            Thread t = new Thread(this);
            t.start();

        }
        //开启线程
        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what == 1){
                    String str = (String) msg.obj;
                    Log.i(TAG, "handleMessage: 线程开启");

                    //保存更新数据
                    SharedPreferences.Editor editorLi = getSharedPreferences("liList", MODE_PRIVATE).edit();
                    editorLi.putInt("liList", liList.size());
                    for (int i = 0; i < liList.size(); i++) {
                        editorLi.putString("li_"+i, liList.get(i));
                    }editorLi.commit();
                    Log.i(TAG, "onCreate: 已经修改liList");

                    SharedPreferences.Editor editorHerf = getSharedPreferences("herfList", MODE_PRIVATE).edit();
                    editorHerf.putInt("herfList", liList.size());
                    for (int i = 0; i < liList.size(); i++) {
                        editorHerf.putString("herf_"+i, liList.get(i));
                    }editorHerf.commit();
                    Log.i(TAG, "onCreate: 已修改HerfList");
                    editorHerf.apply();

                    //保存更新时间
                    editorLi = getSharedPreferences("liList", MODE_PRIVATE).edit();
                    editorLi.putString("update_date",todayDate);
                    editorLi.apply();
                }
                super.handleMessage(msg);
            }
        };

        ListView listView = (ListView) findViewById(R.id.list_result);
        listView.setOnItemClickListener(this);

    }

    //点击按钮查询
    public void search(View btn){
        resultList.clear();
        //获取关键字
        EditText input = findViewById(R.id.search);
        String str = input.getText().toString();
        Log.i(TAG, "search: 获取关键字"+str);

        //查找
        String s;
        for (int n = 0;n<liList.size();n++){
            s= liList.get(n);
            Log.i(TAG, "search: 查找:"+s);
            Log.i(TAG, "search: str:"+str);
            Log.i(TAG, "search: "+s.indexOf(str));
            if (s.indexOf(str)!=-1){
                resultList.add(s);
            }
        }
        if (resultList.size()==0){
            Toast.makeText(this,"不存在数据",Toast.LENGTH_SHORT).show();
            Log.i(TAG, "search: 提示");
        }else{
            //显示
            ListView listView = (ListView) findViewById(R.id.list_result);
            ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,resultList);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void run() {
        Log.i(TAG,"log...");
        Message msg = handler.obtainMessage();
        msg.what = 1;
        msg.obj = "搜索结果";
        handler.sendMessage(msg);

        //获取网络数据
        try {
            URL url = new URL("https://it.swufe.edu.cn/index/tzgg.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream inp = http.getInputStream();

            String html = inputStreamString(inp);
            Log.i(TAG, "run: "+html);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //使用jsoup进行解析
        Document doc = null;
        try {
            //首页数据提取
            Log.i(TAG, "run: 首页数据");
            String urlFirst = "https://it.swufe.edu.cn/index/tzgg.htm";
            doc = Jsoup.connect(urlFirst).get();
            Log.i(TAG, "run: "+doc.title());

            Elements tds = doc.getElementsByTag("td");
            Element tdPage = tds.get(1);
            Log.i(TAG, "run: pageString"+tdPage.text());
            String pageStr = tdPage.text();
            String pageNum = pageStr.substring(pageStr.length()-2,pageStr.length());
            Log.i(TAG, "run: page"+pageNum);
            page = Integer.parseInt(pageNum);


//            int i = 1;
//            for(Element td : tds){
//                Log.i(TAG, "run: td["+i+"]="+td);
//                i++;
//            }

            //获取全部数据数据
            int size1,size2;
            int i = 1;
            String url = "https://it.swufe.edu.cn/index/tzgg.htm";
            for (;i<page+1;i++){
                Log.i(TAG, "run: 第"+i+"页数据");
                Log.i(TAG, "run: url:"+url);
                doc = Jsoup.connect(url).get();
                Elements lis = doc.getElementsByTag("li");
                Elements herfs = doc.select("a[href]");
                size1 = lis.size();
                size2 = herfs.size();
                url = herfs.get(size2-2).attr( "abs:href" ).toString();
                for(int m = 68;m<size1-4;m++){
                    Element lii = lis.get(m);
                    //获取文字
                    Elements span = lii.getElementsByTag("span");
                    liList.add(span.text());
                    Log.i(TAG, "run: span:"+span.text());
                    //获取超链接
                    Element herfi = herfs.get(m+2);
                    herfList.add(herfi.attr( "abs:href" ));
                    Log.i(TAG, "run: herf:"+herfi.attr( "abs:href" ));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String inputStreamString(InputStream inputStream) throws IOException {
        final  int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"UTF-8");
        for(;;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }

    @Override
    //添加事件处理
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = (ListView) findViewById(R.id.list_result);
        String searchResult = (String)listView.getItemAtPosition(position);
        Log.i(TAG, "onItemClick: 点击"+id);
        int searchInt = liList.indexOf(searchResult) ;
        Log.i(TAG, "onItemClick: "+searchResult);
        String searchURL = herfList.get(searchInt);
        Log.i(TAG, "onItemClick: "+searchURL);
        Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchURL));
        startActivity(newIntent);
    }

    //判断两个字符串日期是否在同一周
    public boolean isSameWeek(String date1,String date2){
        try {
            Calendar ca= Calendar.getInstance();//获取Calendar实例
            ca.setTime(DateFormat.getDateInstance().parse(date1));//设置日期
            int a=ca.WEEK_OF_YEAR;//获取一年中的周数
            ca.setTime(DateFormat.getDateInstance().parse(date2));
            int b=ca.WEEK_OF_YEAR;
            Log.i(TAG, "isSameWeek: 更新？"+(a==b));
            return a==b;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
