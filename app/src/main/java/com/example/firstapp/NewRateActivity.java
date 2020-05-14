package com.example.firstapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewRateActivity extends AppCompatActivity {

    String TAG = "rateCalc";
    float rate = 0;
    EditText input2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rate);

        //接受NewRate中传递的参数
        String title = getIntent().getStringExtra("title");
        rate = getIntent().getFloatExtra("rate",0f);

        Log.i(TAG, "onCreate: title="+title);
        Log.i(TAG, "onCreate: rate="+rate);
        ((TextView)findViewById(R.id.title2)).setText(title);

        input2=(EditText)findViewById(R.id.input2);
        //添加文本改变监听器
        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView show = (TextView) NewRateActivity.this.findViewById(R.id.show2);
                if (s.length()>0){
                 float val = Float.parseFloat(s.toString());
                    show.setText(val + "RMB --> "+(100/rate*val));
                }else{
                    show.setText("");
                }
            }
        });
    }
}
