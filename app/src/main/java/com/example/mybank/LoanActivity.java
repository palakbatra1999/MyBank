package com.example.mybank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mybank.Adapters.Loan_Adapter;
import com.example.mybank.Database.Databasehelper;
import com.example.mybank.Models.Loan;
import com.example.mybank.Models.Transaction;
import com.example.mybank.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class LoanActivity extends AppCompatActivity {
    private static final String TAG = "LoanActivity";
    private TextView txttop;
    private RecyclerView loanrecView;
    private BottomNavigationView navigationbar;
    private Loan_Adapter adapter;
    private Databasehelper databasehelper;
    private GetLoans getLoans;
    private Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);
        initViews();
        initBottomNavigationView();
        adapter = new Loan_Adapter(this);
        loanrecView.setAdapter(adapter);
        loanrecView.setLayoutManager(new LinearLayoutManager(this));
        databasehelper = new Databasehelper(this);


       getLaonsadded();
    }

    private void getLaonsadded() {
        getLoans=new GetLoans();
        utils=new Utils(this);
        User user=utils.isUserLoggedIn();

        if(null!=user)
        { getLoans.execute(user.get_id());
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews: started");
        txttop = (TextView) findViewById(R.id.txttop);
        loanrecView = (RecyclerView) findViewById(R.id.loanrecView);
        navigationbar = (BottomNavigationView) findViewById(R.id.navigationbar);
    }

    private void initBottomNavigationView() {
        Log.d("TAG", "initBottomNavigationView: started");
        navigationbar.setSelectedItemId(R.id.menu_item_loan);

        navigationbar.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_stats:
                        Intent intent2 = new Intent(LoanActivity.this, StatsActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                        break;
                    case R.id.menu_item_transaction:
                        Intent intent3 = new Intent(LoanActivity.this, TransferActivityforbottom.class);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent3);
                        break;
                    case R.id.menu_item_home:
                        Intent intent = new Intent(LoanActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case R.id.menu_item_investment:
                        Intent intent1 = new Intent(LoanActivity.this, InvestmentActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        break;
                    case R.id.menu_item_loan:

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
        if(null!=getLoans)
        {
            if(!getLoans.isCancelled())
            {
                getLoans.cancel(true);
            }
        }
    }

    private class GetLoans extends AsyncTask<Integer, Void, ArrayList<Loan>> {
        @Override
        protected ArrayList<Loan> doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databasehelper.getReadableDatabase();
                Cursor cursor = db.query("loans", null, "user_id=?", new String[]{String.valueOf(integers[0])},
                        null, null, "init_date DESC");


                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        ArrayList<Loan> loans = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount(); i++) {
                            Loan loan = new Loan();
                            loan.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                            loan.setFinish_date(cursor.getString(cursor.getColumnIndex("finish_date")));
                            loan.setInit_date(cursor.getString(cursor.getColumnIndex("init_date")));
                            loan.setName(cursor.getString(cursor.getColumnIndex("name")));
                            loan.setTransaction_id(cursor.getColumnIndex("transaction_id"));
                            loan.setUser_id(cursor.getColumnIndex("user_id"));
                            loan.setMonthly_roi(cursor.getColumnIndex("monthly_roi"));
                            loan.setInit_amount(cursor.getColumnIndex("init_amount"));
                            loan.setRemained_amount(cursor.getColumnIndex("remained_amount"));
                            loan.setMonthly_payment(cursor.getColumnIndex("monthly_payment"));
                          //  loan.set
                            loans.add(loan);
                            cursor.moveToNext();
                        }
                        cursor.close();
                        db.close();
                        return loans;
                    } else {
                        cursor.close();
                        db.close();
                        return null;
                    }
                } else {
                    db.close();
                    return null;
                }

            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Loan> loans) {
            super.onPostExecute(loans);

            if (null != loans) {
                adapter.setLoans(loans);
            } else {
                adapter.setLoans(new ArrayList<Loan>());
            }
        }
    }
}
