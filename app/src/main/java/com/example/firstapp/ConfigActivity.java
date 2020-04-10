package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    public final String TAG = "ConfigActivity";
    EditText euroText;
    EditText wonText;
    EditText dollarText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //获得传入数据
        Intent intent = getIntent();
        //取出参数，取不到返回0.0f
        float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
        float won2 = intent.getFloatExtra("won_rate_key",0.0f);
        float dollar2 = intent.getFloatExtra("dollar_rate_key",0.0f);

        Log.i(TAG, "onCreate: euro_rate_key"+euro2);
        Log.i(TAG, "onCreate: won_rate_key"+won2);
        Log.i(TAG, "onCreate: dollar_rate_key"+dollar2);

        //获得三个控件
        euroText = (EditText)findViewById(R.id.euro_rate);
        wonText = (EditText)findViewById(R.id.won_rate);
        dollarText = (EditText)findViewById(R.id.dollar_rate);

        //显示数据到控件
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String.valueOf(won2));
        dollarText.setText(String.valueOf(dollar2));
    }

    public void save(View btn){
        Log.i(TAG, "save: ");
        //获取新的输入数据
        float newEuro = Float.parseFloat(euroText.getText().toString());
        float newWon = Float.parseFloat(wonText.getText().toString());
        float newDollar = Float.parseFloat(dollarText.getText().toString());

        Log.i(TAG, "save: 获取到新的值：");
        Log.i(TAG, "save: newEuro"+newEuro);
        Log.i(TAG, "save: newWon"+newWon);
        Log.i(TAG, "save: newDollar"+newDollar);

        //保存到Bundle或者返回到Extra
         Intent intent = getIntent();
         Bundle bdl = new Bundle();
         bdl.putFloat("key_euro",newEuro);
         bdl.putFloat("key_won",newWon);
         bdl.putFloat("key_dollar",newDollar);
//        intent.putExtra("key_euro",newEuro);
//        intent.putExtra("key_won",newWon);
//        intent.putExtra("key_dollar",newDollar);

        //传参
        intent.putExtras(bdl);
        //intent是把数据带回去的对象
        setResult(2,intent);

        //结束当前页面，返回到调用页面
        finish();
    }
}
