package com.example.mybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.mybank.Database.Databasehelper;
import com.example.mybank.Models.Loan;
import com.example.mybank.Models.Transaction;
import com.example.mybank.Models.User;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.transform.Templates;

public class StatsActivity extends AppCompatActivity {

    private static final String TAG = "StatsActivity";
    private BarChart barchart;
    private PieChart pieChart;
    private BottomNavigationView navigationbar;
    private Databasehelper databasehelper;
    private  Utils utils;
    private GetTransactions getTransactions;
    private GetLoans getLoans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initViews();
        initBottomNavigationView();
        databasehelper=new Databasehelper(this);
        utils=new Utils(this);
        User user=utils.isUserLoggedIn();
        getLoans=new GetLoans();
        getTransactions=new GetTransactions();
        if(null!=user)
        {
            getLoans.execute(user.get_id());
            getTransactions.execute(user.get_id());
        }
    }
    private class GetTransactions extends AsyncTask<Integer,Void, ArrayList<Transaction>>
    {


        @Override
        protected ArrayList<Transaction> doInBackground(Integer... integers) {
            try{
                SQLiteDatabase db=databasehelper.getReadableDatabase();
                Cursor cursor=db.query("transactions",null,null,
                        null,null,null,null);

                if(null!=cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Transaction> arrayList=new ArrayList<>();

                        for(int i=0;i<cursor.getCount();i++)
                        {
                            Transaction transaction=new Transaction();
                            transaction.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            transaction.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
                            transaction.setDate(cursor.getString(cursor.getColumnIndex("date")));
                            transaction.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                            transaction.setRecipient(cursor.getString(cursor.getColumnIndex("recipient")));
                            transaction.setType(cursor.getString(cursor.getColumnIndex("type")));
                            transaction.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));

                            arrayList.add(transaction);
                            cursor.moveToNext();
                       }
                        cursor.close();
                        db.close();
                        return arrayList;
                    }
                    else
                    {
                        cursor.close();
                        db.close();
                        return null;
                    }

                }
                else
                {
                    db.close();
                    return null;
                }
            }
            catch(Exception e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Transaction> transactions) {
            super.onPostExecute(transactions);
            if(null!=transactions)
            {
                Calendar calendar=Calendar.getInstance();
                int currentmonth=calendar.get(Calendar.MONTH);
                int currentyear=calendar.get(Calendar.YEAR);

                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                ArrayList<BarEntry> entries=new ArrayList<>();
                for(Transaction t:transactions)
                {
                    try {
                        Date date=sdf.parse(t.getDate());
                        calendar.setTime(date);
                        int month=calendar.get(Calendar.MONTH);
                        int year=calendar.get(Calendar.YEAR);
                        int day=calendar.get(Calendar.DAY_OF_MONTH)+1;

                        if(month==currentmonth&&year==currentyear) {
                          boolean doesdayexist=false;
                          for(BarEntry e:entries) {
                              if(e.getX()==day)
                                  doesdayexist=true;
                              else doesdayexist=false;
                          }
                          if(doesdayexist) {
                              for(BarEntry e:entries)
                              {
                                  e.setY(e.getY()+(t.getAmount()).floatValue());
                          }

                        }
                          else
                          {
                              entries.add(new BarEntry(day,t.getAmount().floatValue()));
                          }
                    }
                } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                BarDataSet barDataSet=new BarDataSet(entries,"Account Activity");
                barDataSet.setColor(Color.GREEN);
                BarData barData=new BarData(barDataSet);
               barchart.getAxisLeft().setEnabled(false);
                XAxis axis= barchart.getXAxis();
                axis.setAxisMaximum(31);
                axis.setEnabled(false);
                YAxis yAxis= barchart.getAxisLeft();
                yAxis.setAxisMaximum(40);
                yAxis.setAxisMinimum(10);
                yAxis.setDrawGridLines(false);
                Description description=new Description();
                description.setText("All of the accounts transactions");
                description.setTextSize(12f);
                barchart.setData(barData);
                barchart.setDescription(description);

                barchart.invalidate();
            }
            else
            {
                Log.d(TAG, "onPostExecute: Arraylist is null");
            }
        }
        }
        private class GetLoans extends AsyncTask<Integer,Void,ArrayList<Loan>>
        {


            @Override
            protected ArrayList<Loan> doInBackground(Integer... integers) {
                try{
                    SQLiteDatabase db=databasehelper.getReadableDatabase();
                    Cursor cursor=db.query("loans",null,null,
                            null,null,null,null);

                    if(null!=cursor)
                    {
                        if(cursor.moveToFirst())
                        {
                            ArrayList<Loan> arrayList=new ArrayList<>();

                            for(int i=0;i<cursor.getCount();i++)
                            {

                                Loan loan = new Loan();
                                loan.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                                loan.setFinish_date(cursor.getString(cursor.getColumnIndex("finish_date")));
                                loan.setInit_date(cursor.getString(cursor.getColumnIndex("init_date")));
                                loan.setName(cursor.getString(cursor.getColumnIndex("name")));
                                loan.setTransaction_id(cursor.getColumnIndex("transaction_id"));
                                loan.setUser_id(cursor.getColumnIndex("user_id"));
                                loan.setMonthly_roi(cursor.getColumnIndex("monthly_roi"));
                                loan.setInit_date(String.valueOf(cursor.getColumnIndex("init_amount")));
                                loan.setRemained_amount(cursor.getColumnIndex("remained_amount"));
                                loan.setMonthly_payment(cursor.getColumnIndex("monthly_payment"));


                                arrayList.add(loan);
                                cursor.moveToNext();
                            }
                            cursor.close();
                            db.close();
                            return arrayList;
                        }
                        else
                        {
                            cursor.close();
                            db.close();
                            return null;
                        }

                    }
                    else
                    {
                        db.close();
                        return null;
                    }
                }
                catch(Exception e)
                {

                }
                return null;

            }

            @Override
            protected void onPostExecute(ArrayList<Loan> loans) {
                super.onPostExecute(loans);
                if(null!=loans)
                {
                     ArrayList<PieEntry>  entries=new ArrayList<>();
                     double totalLoansAmount=0.0;
                     double totalRemainedamount=0.0;

                     for(Loan l:loans)
                     {
                         totalLoansAmount+=l.getInit_amount();
                         totalRemainedamount+=l.getRemained_amount();
                     }
                    entries.add(new PieEntry((float)totalLoansAmount,"Total Loans"));
                    entries.add(new PieEntry((float)totalRemainedamount,"Total Remained Amount"));
                    PieDataSet dataSet=new PieDataSet(entries,"");
                     dataSet.setColors(ColorTemplate.PASTEL_COLORS);
                     dataSet.setSliceSpace(5f);
                     dataSet.setHighlightEnabled(true);

                    PieData data=new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.animateY(1000, Easing.EaseInBounce);
                    pieChart.setDrawHoleEnabled(false);
                    pieChart.invalidate();



            }
            }
        }

    private void initViews() {
        barchart=(BarChart)findViewById(R.id.barchart);
        pieChart=(PieChart)findViewById(R.id.piechart);
        navigationbar=(BottomNavigationView)findViewById(R.id.navigationbar);
    }

    private void initBottomNavigationView() {
        Log.d("TAG", "initBottomNavigationView: started");
        navigationbar.setSelectedItemId(R.id.menu_item_stats);
        navigationbar.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.menu_item_stats:

                        break;
                    case R.id.menu_item_transaction:
                        Intent intent1=new Intent(StatsActivity.this,TransferActivityforbottom.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        break;
                    case R.id.menu_item_home:
                        Intent intent4=new Intent(StatsActivity.this,MainActivity.class);
                        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent4);
                        break;

                    case R.id.menu_item_investment:
                        Intent intent=new Intent(StatsActivity.this,InvestmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case R.id.menu_item_loan:
                        Intent intent2=new Intent(StatsActivity.this,LoanActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                        break;


                    default:
                        break;

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=getTransactions)
        {
            if(!getTransactions.isCancelled())
            {
                getTransactions.cancel(true);
            }
        }
        if(null!=getLoans)
        {
            if(!getLoans.isCancelled())
            {
                getLoans.cancel(true);
            }
        }
    }
}
