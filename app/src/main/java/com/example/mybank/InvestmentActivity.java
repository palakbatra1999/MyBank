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

import com.example.mybank.Database.Databasehelper;
import com.example.mybank.Models.Investment;
import com.example.mybank.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class InvestmentActivity extends AppCompatActivity {
private RecyclerView investmentRecView ;
private BottomNavigationView  navigationbar;
private InvestmentAdapter investmentAdapter;
private Databasehelper databasehelper;
private  GetInvestments getInvestments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment);
        initViews();
        initBottomNavigationView();

        investmentAdapter=new InvestmentAdapter(InvestmentActivity.this);
        investmentRecView.setAdapter(investmentAdapter);
        investmentRecView.setLayoutManager(new LinearLayoutManager(this));
        databasehelper=new Databasehelper(this);
         getinvest();
    }

    private void getinvest() {
        getInvestments=new GetInvestments();
      Utils utils=new Utils(this);
      User user=utils.isUserLoggedIn();
      if(null!=user)
      {
          getInvestments.execute(user.get_id());
      }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!getInvestments.isCancelled())
        {
            getInvestments.cancel(true);
        }
    }

    private class GetInvestments extends AsyncTask<Integer,Void, ArrayList<Investment>>
{

    @Override
    protected ArrayList<Investment> doInBackground(Integer... integers) {
        try{
            SQLiteDatabase db=databasehelper.getReadableDatabase();
            Cursor cursor=db.query("investments",null,"user_id=?",
                    new String[]{String.valueOf(integers[0])},
                    null,null,"init_date DESC");

          if(null!=cursor)
          {
              if(cursor.moveToFirst())
              {   ArrayList<Investment> arrayList=new ArrayList<Investment>();
                  for(int i=0;i<cursor.getCount();i++)
                  {
                     Investment investment=new Investment();
                     investment.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                      investment.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                      investment.setTransaction_id(cursor.getInt(cursor.getColumnIndex("transaction_id")));
                      investment.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
                      investment.setFinish_date(cursor.getString(cursor.getColumnIndex("finish_date")));
                      investment.setInit_date(cursor.getString(cursor.getColumnIndex("init_date")));
                      investment.setName(cursor.getString(cursor.getColumnIndex("name")));
                      investment.setMonthly_roi(cursor.getDouble(cursor.getColumnIndex("monthly_roi")));

                      arrayList.add(investment);
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
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(ArrayList<Investment> investments) {
        super.onPostExecute(investments);

        if(null!=investments)
        {
            investmentAdapter.setInvestments(investments);
        }
        else
        {
            investmentAdapter.setInvestments(new ArrayList<Investment>());

        }
    }
}
    private void initViews() {
        navigationbar=(BottomNavigationView)findViewById(R.id.navigationbar);
        investmentRecView=(RecyclerView)findViewById(R.id.investmentRecView);
    }

    private void initBottomNavigationView() {
        Log.d("TAG", "initBottomNavigationView: started");
        navigationbar.setSelectedItemId(R.id.menu_item_investment);

        navigationbar.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.menu_item_stats:
                        //TODO :complete this logic
                        break;
                    case R.id.menu_item_transaction:
                        //TODO :complete this logic
                        break;
                    case R.id.menu_item_home:
                        Intent intent=new Intent(InvestmentActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case R.id.menu_item_investment:

                        break;
                    case R.id.menu_item_loan:
                        Intent intent2=new Intent(InvestmentActivity.this,LoanActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                        break;
                    default:
                        break;

                }
            }
        });
    }
}
