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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mybank.Adapters.TransactionAdapter;
import com.example.mybank.Database.Databasehelper;
import com.example.mybank.Models.Transaction;
import com.example.mybank.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TransferActivityforbottom extends AppCompatActivity {

    private static final String TAG ="TranrActivityforbottom" ;
    private TextView filtertxt,txtview,txttransaction,warning;
    private EditText txtedt ;
    private Button  button ;
    private RecyclerView recyclerview ;
    private RadioGroup  rgdtype;
    private BottomNavigationView navigationbar ;
    private TransactionAdapter adapter;
    private Databasehelper databasehelper;
    private GetTransactions getTransactions;
    private  Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_activityforbottom);
        initViews();
        initBottomNavigationView();
        adapter=new TransactionAdapter();
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        
        databasehelper=new Databasehelper(this);
        utils=new Utils(this);
        initsearch();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initsearch();
            }
        });
        rgdtype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initsearch();
            }
        });
    }

    private void initsearch() {
        Log.d(TAG, "initsearch: started");
        getTransactions=new GetTransactions();
        User user=utils.isUserLoggedIn();
        if(null!=user)
        {
            getTransactions.execute(user.get_id());
        }

    }

    private void initViews() {
        navigationbar=(BottomNavigationView)findViewById(R.id.navigationbar);
        filtertxt=(TextView) findViewById(R.id.filtertxt);
        txtview=(TextView) findViewById(R.id.txtview);
        txttransaction=(TextView) findViewById(R.id.txttransaction);
        warning=(TextView) findViewById(R.id.warning);
        txtedt=(EditText) findViewById(R.id.txtedt);
        button=(Button) findViewById(R.id.button);
        recyclerview=(RecyclerView) findViewById(R.id.recyclerview);
        rgdtype=(RadioGroup)findViewById(R.id.rgdtype);



    }

    private void initBottomNavigationView() {
        Log.d("TAG", "initBottomNavigationView: started");
        navigationbar.setSelectedItemId(R.id.menu_item_transaction);
        navigationbar.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.menu_item_stats:
                        Intent intent20=new Intent(TransferActivityforbottom.this,StatsActivity.class);
                        intent20.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent20);
                        break;

                    case R.id.menu_item_transaction:
                        //TODO :complete this logic
                        break;
                    case R.id.menu_item_home:
                        Intent intent1=new Intent(TransferActivityforbottom.this,MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        break;
                    case R.id.menu_item_investment:
                        Intent intent=new Intent(TransferActivityforbottom.this,InvestmentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case R.id.menu_item_loan:
                        Intent intent2=new Intent(TransferActivityforbottom.this,LoanActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                        break;


                    default:
                        break;

                }
            }
        });
    }

    private class GetTransactions extends AsyncTask<Integer,Void, ArrayList<Transaction>>
    {
        private  double min=0.0;
        private  String type="all";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.min=Double.valueOf(txtedt.getText().toString());

            switch ((rgdtype.getCheckedRadioButtonId()))
            {
                case R.id.rb_all:
                      type="all";
                    break;
                case R.id.rb_investment:
                    type="investment";
                    break;

                case R.id.rb_loan:
                    type="loan";
                    break;
                case R.id.rb_profit:
                    type="profit";
                    break;
                case R.id.rb_recieve:
                    type="recieve";
                    break;
                case R.id.rb_send:
                    type="send";
                    break;

                default:
                    type="all";
                    break;
            }
        }

        @Override
        protected ArrayList<Transaction> doInBackground(Integer... integers) {
            try{
                SQLiteDatabase db=databasehelper.getReadableDatabase();
                Cursor cursor;
                if(type.equals("all"))
                {
                    cursor=db.query("transactions",null,"user_id=?",new String[]{String.valueOf(integers[0])},
                            null,null,"date DESC");
                }
                else
                {
                    cursor=db.query("transactions",null,"user_id=? AND type=?",new String[]{String.valueOf(integers[0]),type},
                            null,null,"date DESC");
                }
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

                           double absolute_amount=transaction.getAmount();
                           if(absolute_amount<0)
                           {
                             absolute_amount=-absolute_amount;
                           }
                          if(absolute_amount<this.min)
                           {
                               arrayList.add(transaction);
                           }

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
                        return  null;
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
            { warning.setVisibility(View.INVISIBLE);
                adapter.setTransactions(transactions);
            }
            else
            {
                warning.setVisibility(View.VISIBLE);
                adapter.setTransactions(new ArrayList<Transaction>());
            }
        }
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
    }
}
