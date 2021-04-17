package com.jasonkim.b0413planktimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Button btnAddSession;
    Button btnMain;

    //if it is counting down?
    Boolean isStopped = true;

    //have the current Frag# counting down
    int currentFrag = 0;

    ArrayList<FragActivity> frags = new ArrayList<FragActivity>();

    int fragNo = -1;

    EditText edSessionTitle, edPreTime, edExeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMain = findViewById(R.id.btnMain);
        btnMain.setOnClickListener(new btnMain_Click());

        btnAddSession = findViewById(R.id.btnAddSession);
        btnAddSession.setOnClickListener(new SaveSession());

        edSessionTitle = findViewById(R.id.edSessionTitle);
        edPreTime = findViewById(R.id.edPreTime);
        edExeTime = findViewById(R.id.edTime);


        //Get session data from database
        //Add fragment having session data
        Thread_Add_Session thread_add_session = new Thread_Add_Session(this, edExeTime);
        thread_add_session.start();
    }

    public void Add_Frag(View v, SessionClass session) {
        fragNo++;
        FragActivity fragActivity = new FragActivity(fragNo, session);
        frags.add(fragActivity);
        getSupportFragmentManager().beginTransaction().add(R.id.linearLayout, frags.get(fragNo)).commit();
    }

    public void Remove_Frag(int fragNo, int sessionId) {
        getSupportFragmentManager().beginTransaction().remove(frags.get(fragNo)).commit();
        Database database = new Database(this);
        database.DeleteSessionById(sessionId);
    }

    //    Show session object in main window and make main window disable.
    public void ShowSession(SessionClass session) {

        edSessionTitle.setText(session.sessionTitle);
        edPreTime.setText(session.preTime);
        edExeTime.setText(session.exeTime);

        disableEditText(edSessionTitle);
        disableEditText(edPreTime);
        disableEditText(edExeTime);
    }

    //make main window disable.
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    //    return session class having data from editboxes.
    private SessionClass InputToSession() {
        SessionClass session = new SessionClass();
        session.sessionTitle = edSessionTitle.getText().toString();
        session.preTime = Integer.parseInt(edPreTime.getText().toString());
        session.exeTime = Integer.parseInt(edExeTime.getText().toString());
        return session;
    }

    //    Fill Input text with session data
    private void FillInputWithSessionData(SessionClass session) {
        edSessionTitle.setText(session.sessionTitle);
        edPreTime.setText(session.preTime + "");
        edExeTime.setText(session.exeTime + "");
    }

    // Start plank
//    1. get session info from fragment
//    2. count down and showing remained time
//    3. when the time is expired, find next fragment and repeat from 1 again.
//    4. the button text turn into "STOP"
//    5. preTime and extTime become initialized
//    6. the button text turn into "START"

    private void StartPlank() {
        SessionClass session = frags.get(currentFrag).session;
        Log.d("Session", session.toString());
        FillInputWithSessionData(session);
        CountDownThread countDownThread = new CountDownThread(session);
        countDownThread.run();
    }


//
    class btnMain_Click implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //if counting down stopped
            if (isStopped) {
                btnMain.setText("STOP");
                StartPlank();
                isStopped = false;

            } else {
                btnMain.setText("START");
                isStopped = true;
            }
        }
    }

    class CountDownThread extends Thread {
        SessionClass session;

        public CountDownThread(SessionClass session) {
            this.session = session;
        }

        public void run() {
            while (true) {

                if (session.preTime == 0 && session.exeTime == 0) {
                    break;
                }
                if (session.preTime > 0) {
                    session.preTime--;
                } else {
                    session.exeTime--;
                }
                Log.d("While :", session.toString());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Run :", session.toString());
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


    //pick data from view(input)
    //Store session data into database
    class SaveSession implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SessionClass session = InputToSession();
            Database database = new Database(v.getContext());
            database.SaveSession(session);
            Add_Frag(v, session);
        }
    }

    //Get session data from database
    //Add fragment having session data
    class Thread_Add_Session extends Thread {
        Context context;
        View view;

        public Thread_Add_Session(Context context, View view) {
            this.context = context;
            this.view = view;
        }

        @Override
        public void run() {
            super.run();
            Database database = new Database(context);
            ArrayList<SessionClass> sessionClasses = database.GetAllSessions();
            database.close();

            for (int i = 0; i < sessionClasses.size(); i++) {
                final int k = i;
                final SessionClass sessionClass = sessionClasses.get(k);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Add_Frag(view, sessionClass);
                    }
                });
            }
        }
    }

}


