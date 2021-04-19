package com.jasonkim.b0413planktimer;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    Bundle bundle;
    TextView tvProgress, tvMsg;
    ProgressBar progressBar;
    Handler handler;

    //to pause counting down
    boolean flag = false;

    //When it is called firstly, it will be added.
    int currentSession = -1;
    ArrayList<SessionClass> arraySession = new ArrayList<SessionClass>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bundle = (Bundle) this.getIntent().getExtras();

        tvProgress = findViewById(R.id.tvProgress);
        tvMsg = findViewById(R.id.tvMsg);
        progressBar = findViewById(R.id.progress_bar);
        handler = new Handler();

//      Get session list from database.
        Database database = new Database(this);
        arraySession = database.GetAllSessions();
        database.close();

        //Run with preparing Time
        RunSession(true);
    }

    private void RunSession(boolean isPreTime) {

//      if the last session done
        if (currentSession > (arraySession.size() - 1)) {

            return;
        }

//      if it is pretime, move to the next session
        if(isPreTime){
            currentSession++;
        }
        SessionClass session= arraySession.get(currentSession);
        String title = session.sessionTitle;
        int preTime = session.preTime;
        int exeTime = session.exeTime;

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        if(isPreTime){
            Auto_Count auto_count = new Auto_Count(preTime, isPreTime);
            auto_count.start();
        }else{
            Auto_Count auto_count = new Auto_Count(exeTime, isPreTime);
            auto_count.start();
        }
    }


    class Auto_Count extends Thread {

        int Count;
        boolean isPreTime;

        public Auto_Count(int Count, boolean isPreTime) {
            this.Count = Count;
            this.isPreTime = isPreTime;
        }

        @Override
        public void run() {
            super.run();
            int i = Count;
            while (i >= 0) {
                if (flag) {
                    final int remainTime = Count - i;
                    final int TotalTime = Count;
                    i--;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            UpDate_Progressbar(remainTime, TotalTime);
                        }
                    });
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Run next session with opposite flag.
//            RunSession(!isPreTime);

//            finish Activity
//            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        flag = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        flag = false;
    }
    public void onClick_tvProgress(View view) {

        if (flag) {
            tvMsg.setText("Paused.");
        } else {
            tvMsg.setText("");
        }
        flag = !flag;
    }

    public void UpDate_Progressbar(int remainTime, int totalTime) {

        int prg = remainTime * 100 / totalTime;
        progressBar.setProgress(prg);

        tvProgress.setText(remainTime + " / " + totalTime);
        if (prg > 98) {
            tvMsg.setText("Done.");
        } else if (prg > 70) {
            tvMsg.setText("Right there");
        } else if (prg > 50) {
            tvMsg.setText("Almost there");
        }
        // Log.d("UpDate_Progressbar", prg + "%");
    }
}