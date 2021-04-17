package com.jasonkim.b0413planktimer;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragActivity extends Fragment {

    int fragNo;
    SessionClass session = new SessionClass();
    Handler handler = new Handler();

    Button button;
    TextView tvPreTime, tvExeTime, tvTitle;

    MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.session_frag, container, false);

        mainActivity = (MainActivity) getActivity();

        button = viewGroup.findViewById(R.id.btnRemove);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveSession();
            }
        });

        tvPreTime = viewGroup.findViewById(R.id.tvPreTime);
        tvExeTime = viewGroup.findViewById(R.id.tvTime);
        tvTitle = viewGroup.findViewById(R.id.tvTitle);

        Log.d("Session Title : ",session.sessionTitle);
        SetViews();

        return viewGroup;
    }

    public void StartCountDown(){
        CountDownThread countDownThread = new CountDownThread();
        countDownThread.start();
    }

    class CountDownThread extends Thread {
        public void run() {
            while(true) {

                if(session.preTime == 0 && session.exeTime == 0){
                    //mainActivity.Remove_Frag(session.sessionId);
                }
                if(session.preTime > 0){
                    session.preTime --;
                }else{
                    session.exeTime --;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.ShowSession(session);
                        SetViews();
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void SetViews() {
        button.setText(String.valueOf(session.sessionId));
        tvPreTime.setText(String.valueOf(session.preTime));
        tvExeTime.setText(String.valueOf(session.exeTime));
        tvTitle.setText(String.valueOf(session.sessionTitle));
    }

    public FragActivity(int fragNo, SessionClass session) {
        this.fragNo = fragNo;
        this.session = session;
    }

    public void RemoveSession() {
        mainActivity.Remove_Frag(fragNo, session.sessionId);
    }
}
