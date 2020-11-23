package com.example.mybank;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import com.example.mybank.Adapters.TransactionAdapter;
import com.example.mybank.Authentication.LoginActivity;
import com.example.mybank.Authentication.RegisterUser;
import com.example.mybank.Database.Databasehelper;
import com.example.mybank.Dialoug.AddTransactionDialoug;
import com.example.mybank.Models.Shopping;
import com.example.mybank.Models.Transaction;
import com.example.mybank.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity" ;
    private Utils  utils;
private Toolbar toolbar;
private BottomNavigationView navigationbar;
private FloatingActionButton fbAddTransaction;
private TextView txtwelcome,amount,txtTransactions,notransactions;

private RecyclerView recyclerview;
private  Databasehelper databasehelper;
private GetAccountAmount getAccountAmount;
private TransactionAdapter adapter;
private gettransactions gettransactions;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
             initViews();
             initBottomNavigationView();
             setSupportActionBar(toolbar);
              utils=new Utils(this);
              User user=utils.isUserLoggedIn();

              if(null!=user)
              {
                  Log.d("TAG", "User:"+user.getFirst_name());
                  Toast.makeText(this,"User:"+user.getFirst_name(),Toast.LENGTH_SHORT).show();  }
              else
              {
                  Intent intent=new Intent(this, LoginActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(intent);
              }
              databasehelper=new Databasehelper(this);

              setonclickListeners();
              initTransactionsView();

               stepAmount();
        Log.d(TAG, "onCreate: work"+ WorkManager.getInstance(this).getWorkInfosByTag("profit"));
        Log.d(TAG, "onCreate:loanWork"+ WorkManager.getInstance(this).getWorkInfosByTag("loan_payment"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String message="hey,Checkout my new App";
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,message);
        intent.setType("text/plain");
        Intent chooserIntent=Intent.createChooser(intent,"Send Message via :");
        startActivity(chooserIntent);
        return super.onOptionsItemSelected(item);
    }



    private void initTransactionsView() {
        adapter=new TransactionAdapter();
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
      getTransactions();
    }

    private void getTransactions() {
        gettransactions=new gettransactions();
        Log.d("TAG", "getTransactions: started");
        User user=utils.isUserLoggedIn();
        if(null!=user)
        {
         //   notransactions.setVisibility(View.INVISIBLE);
            gettransactions.execute(user.get_id());
        }
    }

    private void setonclickListeners() {
        Log.d("TAG", "setonclickListeners: strated");
        txtwelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this).
                        setTitle("My Bank").
                        setMessage("created by Palak Batra").
                        setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).
                        setPositiveButton("Visit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.show();

            }
        });

        fbAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTransactionDialoug addTransactionDialoug=new AddTransactionDialoug();
                addTransactionDialoug.show(getSupportFragmentManager(),"add Transaction Dialog");
            }
        });
    }
   

    @Override
    protected void onStart() {
        super.onStart();
        stepAmount();   //the amount may change in between and we want the amount to be displayed currently
        getTransactions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        stepAmount();      //the amount may change in between and we want the amount to be displayed currently
        getTransactions();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=gettransactions)
        {
            if(!gettransactions.isCancelled())
            {
                gettransactions.cancel(true);
            }
        }
        if(null!=getAccountAmount)
        {
            if(!getAccountAmount.isCancelled())
            {
                getAccountAmount.cancel(true);
            }
        }

    }

    private void stepAmount() {
        Log.d("TAG", "stepAmount: started");
       User user=utils.isUserLoggedIn();
       if(null!=user)
       {
           getAccountAmount=new GetAccountAmount();
           getAccountAmount.execute(user.get_id());
       }

    }

    private  class GetAccountAmount extends AsyncTask<Integer,Void,Double>
    {

        @Override
        protected Double doInBackground(Integer... integers) {
            try{
               SQLiteDatabase db=databasehelper.getReadableDatabase();
               Cursor cursor=db.query("users",new String[]{"remained_amount"},"_id=?",
                       new String[]{String.valueOf(integers[0])},null,null,null);
               if(null!=cursor)
               {
                   if(cursor.moveToFirst())
                   {
                      double amount=cursor.getDouble(cursor.getColumnIndex("remained_amount"));
                      cursor.close();
                      db.close();
                     //  Log.d(TAG, "Error: "+amount);
                      return amount;
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
                //Log.d(TAG, "Error: "+e);
                 e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);
            if(aDouble!=null)
            {
              //  Log.d(TAG, "Error: "+aDouble);
                amount.setText(String.valueOf(aDouble));
            }
            else
            {
            //    Log.d(TAG, "stepAmount: stopped due to error");
                amount.setText(String.valueOf(+0.0));
            }
        }
    };
    private class gettransactions extends AsyncTask<Integer,Void, ArrayList<Transaction>>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Transaction> doInBackground(Integer... integers) {
            try
            {
                SQLiteDatabase db=databasehelper.getReadableDatabase();
                Cursor cursor=db.query("transactions",null,"user_id=?",
                        new String[]{String.valueOf(integers[0])},null,null,"date DESC");
                if(null!=cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        ArrayList<Transaction> transactions=new ArrayList<>();
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
                            transactions.add(transaction);
                            cursor.moveToNext();
                        }
                        cursor.close();
                        db.close();
                        return transactions;
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
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Transaction> transactions) {
            super.onPostExecute(transactions);
            if(null!=transactions)
            {
                notransactions.setVisibility(View.INVISIBLE);
                adapter.setTransactions(transactions);

            }
            else
            {
                notransactions.setVisibility(View.VISIBLE);
                adapter.setTransactions(new ArrayList<Transaction>());
            }
        }
    };


    private void initBottomNavigationView() {
        Log.d("TAG", "initBottomNavigationView: started");
        navigationbar.setSelectedItemId(R.id.menu_item_home);
        navigationbar.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.menu_item_stats:
                        Intent intent3=new Intent(MainActivity.this,StatsActivity.class);
                       // intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent3);
                        break;
                    case R.id.menu_item_transaction:
                        Intent intent1=new Intent(MainActivity.this,TransferActivityforbottom.class);
                      //  intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        break;
                    case R.id.menu_item_home:
                        //TODO :complete this logic
                        break;
                    case R.id.menu_item_investment:
                        Intent intent=new Intent(MainActivity.this,InvestmentActivity.class);
                      //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case R.id.menu_item_loan:
                        Intent intent2=new Intent(MainActivity.this,LoanActivity.class);
                       // intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                        break;


                    default:
                        break;

                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        recyclerview=(RecyclerView)findViewById(R.id.recyclerview);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        navigationbar=(BottomNavigationView)findViewById(R.id.navigationbar);
        fbAddTransaction=(FloatingActionButton)findViewById(R.id.fbAddTransaction);
        txtwelcome=(TextView) findViewById(R.id.txtwelcome);
        notransactions=(TextView) findViewById(R.id.notransactions);
        amount=(TextView)findViewById(R.id.amount);
        txtTransactions=(TextView)findViewById(R.id.txtTransactions);

    }
}
