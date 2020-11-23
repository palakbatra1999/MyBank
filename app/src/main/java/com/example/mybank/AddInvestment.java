package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mybank.Database.Databasehelper;
import com.example.mybank.Models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AddInvestment extends AppCompatActivity {
    private static final String TAG ="AddInvestment" ;
    private TextView headingtxt,name_investmentdatetxt,initialamountdatetxt, monthlyroidatetxt,startdatetxt, finishdatetxt,warning;
   private EditText name_investmentdateedttxt,initialamountdateedttxt, monthlyroidateedttxt, startdateedttxt, finishdateedttxt;
    private Button btn_start, btn_end,btn;
    private Databasehelper databasehelper;
    private Utils utils;
    private Addinvestment addinvestment;
    private  Addtransaction addtransaction;
    private Calendar calendar=Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            startdateedttxt.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        }
    };
    private DatePickerDialog.OnDateSetListener dateSet=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            finishdateedttxt.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_investment);

        initView();
        databasehelper=new Databasehelper(AddInvestment.this);
        utils=new Utils(this);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddInvestment.this,
                        dateSetListener,calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddInvestment.this,
                        dateSet,calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidateData()==true)
                {
                    warning.setVisibility(View.GONE);
                    initAdding();
                }
                else
                {
                    warning.setVisibility(View.VISIBLE);
                    warning.setText("Fill All The Blanks!");
                }
            }
        });
    }

    private void initAdding() {
        Log.d(TAG, "initAdding: started");
       addtransaction=new Addtransaction();
       User user= utils.isUserLoggedIn();
       if(null!=user)
       addtransaction.execute(user.get_id());
    }

    private boolean ValidateData() {
        Log.d(TAG, "ValidateData: started");

        if(name_investmentdateedttxt.getText().toString().equals(""))
        {
            return false;
        }
        if(initialamountdateedttxt.getText().toString().equals(""))
        {
            return false;
        }
        if(monthlyroidateedttxt.getText().toString().equals(""))
        {
            return false;
        }
        if(startdateedttxt.getText().toString().equals(""))
        {
            return false;
        }
        if(finishdateedttxt.getText().toString().equals(""))
        {
            return false;
        }
        return true;
    }

    private void initView() {
        btn_start=(Button)findViewById(R.id.btn_start);
        btn_end=(Button)findViewById(R.id.btn_end);
        btn=(Button)findViewById(R.id.btn);
        name_investmentdateedttxt=(EditText) findViewById(R.id.name_investmentdateedttxt);
        initialamountdateedttxt=(EditText) findViewById(R.id.initialamountdateedttxt);
        monthlyroidateedttxt=(EditText) findViewById(R.id.monthlyroidateedttxt);
        startdateedttxt=(EditText) findViewById(R.id. startdateedttxt);
        finishdateedttxt=(EditText) findViewById(R.id.finishdateedttxt);
        headingtxt=(TextView) findViewById(R.id.headingtxt);
        name_investmentdatetxt=(TextView) findViewById(R.id.name_investmentdatetxt);
        initialamountdatetxt=(TextView) findViewById(R.id.initialamountdatetxt);
        monthlyroidatetxt=(TextView) findViewById(R.id.monthlyroidatetxt);
        startdatetxt=(TextView) findViewById(R.id.startdatetxt);
        finishdatetxt=(TextView) findViewById(R.id.finishdatetxt);
        warning=(TextView) findViewById(R.id.warning);



    }

    private class  Addtransaction  extends AsyncTask<Integer ,Void,Integer>
    {
        private  String date,name;
        private  double amount;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.date=startdateedttxt.getText().toString();
            this.name=name_investmentdateedttxt.getText().toString();
            this.amount=-Double.valueOf(initialamountdateedttxt.getText().toString());

        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db=databasehelper.getReadableDatabase();
                ContentValues contentValues=new ContentValues();
                contentValues.put("amount",amount);
                contentValues.put("recipient",name);
                contentValues.put("date",date);
                contentValues.put("type","investment");
                contentValues.put("description","Investment for  "+ name+"  investment");
                contentValues.put("user_id",integers[0]);

                long id=db.insert("transactions",null,contentValues);
                return (int)id;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(null!=integer)
            {
                addinvestment=new Addinvestment();
                User user= utils.isUserLoggedIn();
                if(null!=user)
                    addinvestment.execute(integer);
            }
            else
            {
                Log.d(TAG, "onPostExecute: idn ull");
            }
        }
    }
    private class Addinvestment extends  AsyncTask<Integer,Void,Void>
    {
        private int userid;
        private  String initDate,finishDate,name;
        private  double monthlyroi,amount;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.amount=Double.valueOf(initialamountdateedttxt.getText().toString());
            this.monthlyroi=Double.valueOf(monthlyroidateedttxt.getText().toString());

            this.initDate=startdateedttxt.getText().toString();
            this.finishDate=finishdateedttxt.getText().toString();
            this.name=name_investmentdateedttxt.getText().toString();

            User user=utils.isUserLoggedIn();
            if(null!=user)
            {
                this.userid= user.get_id();
            }
            else
            {
                this.userid=-1;
            }

        }

        @Override
        protected Void doInBackground(Integer... integers) {
            if(userid!=-1)
            {
               try
               {
                 SQLiteDatabase db=databasehelper.getReadableDatabase();

                 ContentValues contentValues=new ContentValues();
                 contentValues.put("name",name);
                 contentValues.put("init_date",initDate);
                   contentValues.put("finish_date",finishDate);
                   contentValues.put("monthly_roi",monthlyroi);
                   contentValues.put("user_id",userid);
                   contentValues.put("transaction_id",integers[0]);
                   contentValues.put("amount",amount);

                   long _id=db.insert("investments",null,contentValues);
                   if(_id!=-1)
                   {
                       Cursor cursor=db.query("users",new String[]{"remained_amount"},"_id=?",new String[]{String.valueOf(userid)},
                               null,null,null,null);

                       if(null!=cursor)
                       {
                           if(cursor.moveToFirst())
                           {
                               double currentremainedamount=cursor.getDouble(cursor.getColumnIndex("remained_amount"));
                               cursor.close();
                               ContentValues values=new ContentValues();
                               values.put("remained_amount",currentremainedamount-amount);
                             int number_affected_rows=  db.update("users",values,"_id=?",new String[]{String.valueOf(userid)});
                               Log.d(TAG, "doInBackground:number_affected_rows "+number_affected_rows);
                           }
                           else
                           {

                               Log.d(TAG, "doInBackground:number_affected_rows 0 ");
                           }
                       }
                       else
                       {

                           cursor.close();
                           Log.d(TAG, "doInBackground:number_affected_rows 0 ");
                       }
                       cursor.close();
                   }

                 db.close();
                  // return null ;
               }
               catch(Exception e)
               {
e.printStackTrace();

               }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent intent=new Intent(AddInvestment.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      if(null!=addtransaction)
      {
          if(!addtransaction.isCancelled())
          {
              addtransaction.cancel(true);
          }
      }
      if(null!=addinvestment)
      {
          if(!addinvestment.isCancelled())
          {
              addinvestment.cancel(true);
          }
      }
    }
}
