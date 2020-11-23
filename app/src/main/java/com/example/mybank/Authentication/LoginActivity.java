package com.example.mybank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mybank.Database.Databasehelper;
import com.example.mybank.MainActivity;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils;

public class LoginActivity extends AppCompatActivity {
private ImageView  image ,logo2;
private TextView  warning,trytologin,register ;
private EditText  textemail,textpassword  ;
private Button   loginbtn ;
private Databasehelper databasehelper;
private  loginuser loginuser;
private  doesemailexist doesemailexist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
     register.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(LoginActivity.this, RegisterUser.class);
      //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
});

     loginbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             initLogin();
         }
     });
    }

    private void initLogin() {
        Log.d("TAG", "initLogin: Started");
        if(!textemail.toString().equals(""))
        {
            if(!textpassword.toString().equals(""))
            {
                warning.setVisibility(View.GONE);
               doesemailexist=new doesemailexist();
               doesemailexist.execute(textemail.getText().toString());
            }
            else
            {
                warning.setVisibility(View.VISIBLE);
                warning.setText("Please enter your password");
            }
        }
        else
        {
            warning.setVisibility(View.VISIBLE);
            warning.setText("Please enter your email");
        }
    }

    private void initViews() {
        image=(ImageView)findViewById(R.id.logo);
        logo2=(ImageView)findViewById(R.id.logo2);
        warning=(TextView) findViewById(R.id.warning);
        trytologin=(TextView)findViewById(R.id.trytologin);
        register=(TextView)findViewById(R.id.register);
        textemail=(EditText) findViewById(R.id. textemail);
        textpassword =(EditText) findViewById(R.id.textpassword );
        loginbtn=(Button)findViewById(R.id.loginbtn);
    }

    private class doesemailexist extends AsyncTask<String,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databasehelper=new Databasehelper(LoginActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                SQLiteDatabase db=databasehelper.getReadableDatabase();
                Cursor cursor=db.query("users",new String[]{"email"},"email=?",
                        new String[]{strings[0]},null,null,null);
                if(null!=cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        cursor.close();
                        db.close();
                        return true;
                    }
                    else
                    {
                        cursor.close();
                        db.close();
                        return false;
                    }

                }else
                {
                    db.close();
                    return false;
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {

                loginuser=new loginuser();
                loginuser.execute();
            }
            else
            {
                warning.setVisibility(View.VISIBLE);
                warning.setText("There is no such user exists,Please enyer a valid email");
            }
        }
    }
    private class loginuser extends  AsyncTask<Void,Void,User>
    {
        private String email;
        private String password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email=textemail.getText().toString();
            password=textpassword.getText().toString();
        }

        @Override
        protected User doInBackground(Void... voids) {

            try{
                SQLiteDatabase db=databasehelper.getReadableDatabase();
                Cursor cursor=db.query("users",null,"email=? AND password=?",
                        new String[]{email,password},null,null,null );
                if(null!=cursor)
                {
                    if(cursor.moveToFirst())
                    {
                        User user=new User();
                        user.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                        user.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                        user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                        user.setFirst_name(cursor.getString(cursor.getColumnIndex("first_name")));
                        user.setLast_name(cursor.getString(cursor.getColumnIndex("last_name")));
                        user.setRemained_amount(cursor.getDouble(cursor.getColumnIndex("remained_amount")));
                        user.setImage_url(cursor.getString(cursor.getColumnIndex("image_url")));

                        cursor.close();
                        db.close();
                        return user;
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
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(null!=user)
            {
                Utils utils=new Utils(LoginActivity.this);
                utils.addusertosharedpreferences(user);
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
            else
            {
                warning.setVisibility(View.VISIBLE);
                warning.setText("Incorrect Password");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=doesemailexist)
        {
            if(!doesemailexist.isCancelled())
            {
                doesemailexist.cancel(true);
            }
        }
        if(null!=loginuser)
        {
            if(!loginuser.isCancelled())
            {
                loginuser.cancel(true);
            }
        }
    }
}
