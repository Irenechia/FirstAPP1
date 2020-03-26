package com.example.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreA;
   TextView scoreB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreA = (TextView) findViewById(R.id.AScore);
        scoreB = (TextView) findViewById(R.id.BScore);
    }

    public void btnAdd1(View btn){
        if(btn.getId()== R.id.score1) {
            showScore(1);
        } else{
            showScore2(1);
        }
    }
    public void btnAdd2(View btn){
        if(btn.getId()== R.id.score2) {
            showScore(2);
        } else{
            showScore2(2);
        }

    }
    public void btnAdd3(View btn){
        if(btn.getId()== R.id.score3) {
            showScore(3);
        } else{
            showScore2(3);
        }
    }

    public void btnReset(View btn){
        scoreA.setText("0");
        scoreB.setText("0");
    }

    private void showScore(int sc){
        Log.i("showScore","inc+"+sc);
        String oddScore = (String)scoreA.getText();
        int newScore = Integer.parseInt(oddScore)+sc;
        scoreA.setText(""+newScore);
    }

    private void showScore2(int sc){
        Log.i("showScore","inc+"+sc);
        String oddScore = (String)scoreB.getText();
        int newScore = Integer.parseInt(oddScore)+sc;
        scoreB.setText(""+newScore);
    }
}
