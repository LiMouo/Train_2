package com.ziker.train.Utils.ToolClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySqLiteOpenHelper extends SQLiteOpenHelper {
    private static MySqLiteOpenHelper mInstance = null;
    private static final int version = 2;
    private static final String CREATE_TABLE_USER= "create table User(id integer primary key autoincrement,username text,password text,sex integer,image text,telephone text)";
    private static final String CREATE_TABLE_ETC = "create table use(id integer primary key autoincrement,CARID integer,USER text,DATE text,MONEY integer)";
    private static final String CREATE_TABLE_ENV = "create table Environment(id integer primary key autoincrement,"+
            "temperature integer,humidity integer,LightIntensity integer,co2 integer,pm2_5 integer,Road integer,date text)";
    private static final String CREATE_TABLE_ACC = "create table Account(id integer primary key autoincrement,"+
            "CARID text,MONEY integer,balance integer,user text,date text)";
    private static final String CREATE_TABLE_VIO = "create table Violation(id integer primary key autoincrement,"+
            "carnumber text,pcode text,paddr text,pdatetime text)";

    public MySqLiteOpenHelper(@Nullable Context context) {
        super(context, "INFO", null, version);
    }

    public synchronized static MySqLiteOpenHelper getInstance(Context context){
        if(mInstance == null)
            mInstance = new MySqLiteOpenHelper(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ETC);
        db.execSQL(CREATE_TABLE_ENV);
        db.execSQL(CREATE_TABLE_ACC);
        db.execSQL(CREATE_TABLE_VIO);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists use");
        db.execSQL("drop table if exists Environment");
        db.execSQL("drop table if exists Account");
        db.execSQL("drop table if exists Violation");
        db.execSQL("drop table if exists User");
        db.execSQL(CREATE_TABLE_ETC);
        db.execSQL(CREATE_TABLE_ENV);
        db.execSQL(CREATE_TABLE_ACC);
        db.execSQL(CREATE_TABLE_VIO);
        db.execSQL(CREATE_TABLE_USER);
    }
}

