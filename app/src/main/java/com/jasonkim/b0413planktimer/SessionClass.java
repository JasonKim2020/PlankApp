package com.jasonkim.b0413planktimer;

import androidx.annotation.NonNull;

public class SessionClass {
    //Session Id
    public int sessionId;

    //Session Title
    public String sessionTitle = "Title";

    //preparation Time
    public int preTime = 3;

    //Exercie time
    public int exeTime = 10;

    @NonNull
    @Override
    public String toString() {
        String str;
        str = "Id: " + sessionId
                + ", Title: " + sessionTitle
                + ", PreTime: " + preTime
                + ", ExeTime: " + exeTime;

        return str;
    }
}
