package com.example.firstapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{

    String data[] = {"wait..."};
    Handler handler;
    public final String TAG = "ListActivity";
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //父类自带布局，不用自己布局
        //setContentView(R.layout.activity_rate_list);

        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY,"");
        Log.i("List", "lastRateDateStr:="+logDate);

        List list1 = new ArrayList<String>();
        for (int i=1; i<100;i++){
            list1.add("item"+i);
        }

        //list用adapter管理
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list1);
        setListAdapter(adapter);

        Thread thread = new Thread(this);
        thread.start();

        handler = new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==7){
                    List<String> list2 = (List<String>)msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };

    }

    @Override
    public void run() {
        List<String> relist = new ArrayList<String>();
        String curDateStr = (new SimpleDateFormat("yyy-MM-dd")).format(new Date());
        Log.i(TAG, "curDateStr: "+curDateStr+";logDate:"+logDate );

        if (curDateStr.equals(logDate)){
            Log.i("run", "日期相等，从数据库中获得数据");
            RateManager manager = new RateManager(this);
            for (RateItem item : manager.listAll()){
                relist.add(item.getCurName()+"=>"+item.getCurRate());
            }


        }else{
            Log.i("run", "日期不等，从网络中获得数据");
            Document doc = null;
            try {
                doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
                Log.i(TAG, "run:" + doc.title());
                Elements tables = doc.getElementsByTag("table");
            /*int i = 1;
            for(Element table :tables){
                Log.i(TAG, "run: tables["+i+"]"+table);
                i++;
            }*/

                //获取table
                Element table2 = tables.get(1);
                Log.i(TAG, "run: table6" + table2);

                //获取td中的数据
                Elements tds = table2.getElementsByTag("td");
                List<RateItem> rateList = new ArrayList<RateItem>();

                for (int i = 0; i < tds.size(); i += 8) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);

                    String str1 = td1.text();
                    String val = td2.text();

                    Log.i(TAG, "run: text=" + td1.text() + "→" + td2.text());
                    relist.add(str1+"→"+val);

                    rateList.add(new RateItem(str1,val));
                }

                //把数据写入数据库
                RateManager manager = new RateManager(this);
                manager.deleteAll();
                manager.addAll(rateList);

                //记录更新日期
                SharedPreferences sp = getSharedPreferences("myrate",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(DATE_SP_KEY,curDateStr);
                editor.commit();
                Log.i("run:","更新日期结束："+curDateStr);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //获取网络数据。放回list中
            Message msg = handler.obtainMessage(7);
            msg.obj = relist;
            handler.sendMessage(msg);
        }

    }

}
