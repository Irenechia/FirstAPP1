package com.example.firstapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {

    TextView scoreA;
    TextView scoreB;
    public final String TAG  = "AppCompatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreA = (TextView) findViewById(R.id.AScore);
        scoreB = (TextView) findViewById(R.id.BScore);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        String scorea =((TextView) findViewById(R.id.AScore)).getText().toString() ;
        String scoreb =((TextView) findViewById(R.id.BScore)).getText().toString();

        outState.putString("teama_score",scorea);
        outState.putString("teamb_score",scoreb);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String scorea = savedInstanceState.getString("teama_score");
        String scoreb = savedInstanceState.getString("teamb_score");

        Log.i(TAG, "onRestoreInstanceState: ");
        ((TextView) findViewById(R.id.AScore)).setText(scorea);
        ((TextView) findViewById(R.id.BScore)).setText(scoreb);
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
