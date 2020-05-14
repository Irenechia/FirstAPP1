package com.example.firstapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener , AdapterView.OnItemLongClickListener {

    public final String TAG = "MyList2Activity";

    Handler handler;
    private ArrayList<HashMap<String,String>> listItem; //存放文字、图片
    private SimpleAdapter listItemAdapter;  //适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_list2);
        initListView();
        this.setListAdapter(listItemAdapter);

        Thread thread = new Thread(this);
        thread.start();

        handler = new Handler(){
            public void handleMessage(Message msg){
                if (msg.what==7){
                    List<HashMap<String,String>> list2 = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this,list2,
                            R.layout.activity_my_list2,//布局用于描述该行的布局
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail }
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            }
        };
        //返回列表控件
        getListView().setOnItemClickListener(this);
    }

    private void initListView(){
        listItem = new ArrayList<HashMap<String, String>>();
        for (int i = 0;i<10;i++){
            HashMap<String,String> map =new HashMap<String, String>();
            map.put("ItemTitle","Rate:"+i); //标题文字
            map.put("ItemDetail","detail:"+i); //详情描述
            listItem.add(map);
        }

        //生成适配器的Item和动态数组对应元素
        listItemAdapter = new SimpleAdapter(this,listItem,
                R.layout.activity_my_list2,//布局用于描述该行的布局
                new String[]{"ItemTitle","ItemDetail"},
                new int[]{R.id.itemTitle,R.id.itemDetail }
        );
    }

    @Override
    public void run() {
        List<HashMap<String,String>> relist = new ArrayList<HashMap<String,String>>();

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
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetail",val);
                relist.add(map);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取网络数据。放回list中
        Message msg = handler.obtainMessage(7);
        msg.obj = relist;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //点击listview后从map中获取对象
        HashMap<String,String> map = (HashMap<String,String>)getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");

        Log.i(TAG,"onItemClick:titleStr="+titleStr);
        Log.i(TAG,"ItemDetail:detailStr="+detailStr);

        //从控件中活儿对象
        TextView title = (TextView)view.findViewById(R.id.itemTitle);
        TextView detail = (TextView)view.findViewById(R.id.itemDetail);
        String title2 = (String) title.getText();
        String detail2 = (String) detail.getText();

        Log.i(TAG,"onItemClick:title2="+title2);
        Log.i(TAG,"onItemClick:detail2="+detail2);

        //打开新的页面传输参数
        Intent rateCalc = new Intent(this,NewRateActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position="+position);
        //删除操作
//        listItem.remove(position);
//        listItemAdapter.notifyDataSetChanged();

        //构造对话框进行确认操作
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override//匿名对象创建
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 对话框事件处理");
                listItem.remove(position);
                listItemAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("否",null);

        Log.i(TAG, "onItemLongClick: size="+listItem.size());
        //false短按事件生效
        return true;
    }
}
