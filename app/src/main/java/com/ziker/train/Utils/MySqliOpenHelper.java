package com.ziker.train.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySqliOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE = "create table use(id integer primary key autoincrement,CARID integer,USER text,DATE text,MONEY integer)";
    private Context context;
    public MySqliOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists use");
        db.execSQL(CREATE_TABLE);
    }


}
