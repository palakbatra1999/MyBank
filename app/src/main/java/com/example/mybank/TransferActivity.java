package com.example.mybank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mybank.Database.Databasehelper;
import com.example.mybank.Models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TransferActivity extends AppCompatActivity {
    private static final String TAG ="Transfer_Activity" ;
    private TextView   amon,recipien,date,description,warning;
private EditText  amount,Recipient,datetxt,descriptionedttxt;

private RadioGroup rgdtype ;
private  Utils utils;
private AddTransfer addTransfer;
private Button  pick_date,add ;

private Databasehelper databasehelper;
    private Calendar calendar=Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            datetxt.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        initViews();
        utils=new Utils(this);
        pick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TransferActivity.this,
                        dateSetListener,calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
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

       addTransfer=new AddTransfer();
        User user= utils.isUserLoggedIn();
        if(null!=user)
         {  addTransfer.execute(user.get_id());
         }
         else 
        {
            Log.d(TAG, "initAdding: stopped due to error");
        }
      
    }

    private void initViews() {
        amon=(TextView)findViewById(R.id.amon);
        warning=(TextView)findViewById(R.id.warning);
        recipien=(TextView)findViewById(R.id.recipien);
        date=(TextView)findViewById(R.id.date);
        description=(TextView)findViewById(R.id.description);
        amount=(EditText) findViewById(R.id.amount);
        Recipient=(EditText)findViewById(R.id.Recipient);
        datetxt=(EditText)findViewById(R.id.datetxt);
        descriptionedttxt=(EditText)findViewById(R.id.descriptionedttxt);
        rgdtype=(RadioGroup)findViewById(R.id.rgdtype);
        pick_date=(Button)findViewById(R.id.pick_date);
        add=(Button)findViewById(R.id.add);

    }
    private boolean ValidateData() {
        Log.d(TAG, "ValidateData: started");

        if(amount.getText().toString().equals(""))
        {
            return false;
        }
        if(Recipient.getText().toString().equals(""))
        {
            return false;
        }
        if(datetxt.getText().toString().equals(""))
        {
            return false;
        }

        return true;
    }

    private class  AddTransfer  extends AsyncTask<Integer ,Void,Void> {
        private  String date,recipient_name,description,type;
        private  double amountt;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.date=datetxt.getText().toString();
            this.recipient_name=Recipient.getText().toString();
            this.amountt=Double.valueOf(amount.getText().toString());
            this.description=descriptionedttxt.getText().toString();

            int radio=rgdtype.getCheckedRadioButtonId();
            switch(radio)
            {
                case R.id.send:
                    type="send";
                    amountt=-amountt;
                    break;
                case R.id.recieve:
                    type="receive";
                    break;
                default:
                    break;
            }

            databasehelper=new Databasehelper(TransferActivity.this);
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                SQLiteDatabase db = databasehelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("amount", amountt);
                contentValues.put("recipient", recipient_name);
                contentValues.put("date", date);
                contentValues.put("type", type);
                contentValues.put("description", description);
                contentValues.put("user_id", integers[0]);

                long id = db.insert("transactions", null, contentValues);
                Log.d(TAG, "doInBackground: _id" + id);

                if (id != -1) {
                    Cursor cursor = db.query("users", new String[]{"remained_amount"},
                            "_id=?", new String[]{String.valueOf(integers[0])}, null, null, null);

                    if (null != cursor) {
                        if (cursor.moveToFirst()) {
                            double currentremainedamount = cursor.getDouble(cursor.getColumnIndex("remained_amount"));
                            cursor.close();
                            ContentValues values = new ContentValues();
                            values.put("remained_amount", currentremainedamount + amountt);
                            int number_affected_rows = db.update("users", values, "_id=?", new String[]{String.valueOf(integers[0])});
                            Log.d(TAG, "doInBackground:number_affected_rows " + number_affected_rows);
                            db.close();
                        } else {
                            cursor.close();
                            db.close();
                            Log.d(TAG, "doInBackground:number_affected_rows 0 ");
                        }

                    }
                    else
                 {   db.close();}
                }
            }
            catch(Exception e)
            {
              e.printStackTrace();

            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent=new Intent(TransferActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      if(null!=addTransfer)
        {if(!addTransfer.isCancelled())
        {
            addTransfer.cancel(true);
        }}
    }
}
