package com.example.firstapp;

import android.app.ListActivity;
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
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{

    String data[] = {"one","two","three"};
    Handler handler;
    public final String TAG = "ListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //父类自带布局，不用自己布局
        //setContentView(R.layout.activity_rate_list);

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
            for (int i = 0; i < tds.size(); i += 8) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);

                String str1 = td1.text();
                String val = td2.text();

                Log.i(TAG, "run: text=" + td1.text() + "→" + td2.text());
                relist.add(str1+"→"+val);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取网络数据。放回list中
        Message msg = handler.obtainMessage(7);
        msg.obj = relist;
        handler.sendMessage(msg);
    }


}
