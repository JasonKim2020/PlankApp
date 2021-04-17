package com.jasonkim.b0413planktimer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "DB_PLANK";
    private static final int VERSION = 1;


    private static final String TB_SESSION = "TB_SESSION";
    private static final String COL_SESSIONID = "COL_SESSIONID";
    private static final String COL_TITLE = "COL_TITLE";
    private static final String COL_PRETIME = "COL_PRETIME";
    private static final String COL_EXETIME = "COL_EXETIME";

    public Database(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public void SaveSession(SessionClass session) {
        String query = "INSERT INTO " + TB_SESSION + "(" +
                COL_TITLE + ", " + COL_PRETIME + ", " + COL_EXETIME + ") " +
                " VALUES (" +
                "'" + session.sessionTitle + "', " +
                session.preTime + ", " +
                session.exeTime + ")";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(query);
        Log.d("SaveQuiz", query);
    }

    public ArrayList<SessionClass> GetAllSessions() {
        ArrayList<SessionClass> sessionClasses = new ArrayList<>();

        SessionClass sessionClass = new SessionClass();
        String query = "SELECT " + COL_SESSIONID + " FROM " + TB_SESSION;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                sessionClass = GetSessionbyId(cursor.getInt(0));
                sessionClasses.add(sessionClass);
            } while (cursor.moveToNext());
        }
        return sessionClasses;
    }

    public SessionClass GetSessionbyId(int sessionId) {

        SessionClass sessionClass = new SessionClass();
        String query = "SELECT " + COL_SESSIONID + ", " +
                COL_TITLE + ", " +
                COL_PRETIME + ", " +
                COL_EXETIME + " FROM " + TB_SESSION + " WHERE " +
                COL_SESSIONID + " = " + sessionId;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            sessionClass.sessionId = cursor.getInt(0);
            sessionClass.sessionTitle = cursor.getString(1);
            sessionClass.preTime = cursor.getInt(2);
            sessionClass.exeTime = cursor.getInt(3);
        }
        sqLiteDatabase.close();
        return sessionClass;
    }

    public void DeleteSessionById(int sessionId) {
        String query = "DELETE FROM " + TB_SESSION + " WHERE " + COL_SESSIONID + " = " + sessionId;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TB_SESSION + " ( " + COL_SESSIONID + " integer PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " text, " +
                COL_PRETIME + " integer, " +
                COL_EXETIME + " integer)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE if Exists " + TB_SESSION;
        db.execSQL(query);
        Log.d("Database onUpgrade", query);
        onCreate(db);
    }
}
