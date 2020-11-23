package com.example.mybank.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class Databasehelper extends SQLiteOpenHelper {
    private static final String DB_NAME="My_BANK";
    private static  final int db_VERSION=1;
    private static  final String TAG="Database_helper";
    public Databasehelper(@Nullable Context context) {
        super(context,DB_NAME, null,  db_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: created");
        String createusertable="CREATE TABLE users(_id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT NOT NULL,password TEXT NOT NULL,"+
                "first_name TEXT,last_name TEXT,address TEXT,image_url TEXT,remained_amount DOUBLE)";



        String createLoansTable="CREATE TABLE loans (_id INTEGER PRIMARY KEY AUTOINCREMENT,init_date DATE," +
                "finish_date DATE,init_amount DOUBLE,remained_amount DOUBLE,monthly_payment DOUBLE,name TEXT,user_id INTEGER,transaction_id INTEGER,monthly_roi DOUBLE)";

        String createInvestmentTable="CREATE TABLE investments(_id INTEGER PRIMARY KEY AUTOINCREMENT,amount DOUBLE," +
                "monthly_roi DOUBLE,name TEXT," +
                "init_date DATE,finish_date DATE ,user_id INTEGER,transaction_id INTEGER)";

        String createTransactionTable="CREATE TABLE transactions(_id INTEGER PRIMARY KEY AUTOINCREMENT,amount DOUBLE," +
                "date DATE,type TEXT,user_id INTEGER,recipient TEXT,description TEXT)";



        db.execSQL(createusertable);
        db.execSQL(createLoansTable);
        db.execSQL(createInvestmentTable);
        db.execSQL(createTransactionTable);


        add(db);

    }
    public void add(SQLiteDatabase db)
    {
        Log.d(TAG, " item added");
        ContentValues contentValues =new ContentValues();
        contentValues.put("name","Bike");
        contentValues.put("image_url","data:image/jpeg;base64,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");
        contentValues.put("description","Perfect Mountain Bike");
        db.insert("items",null,contentValues);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
