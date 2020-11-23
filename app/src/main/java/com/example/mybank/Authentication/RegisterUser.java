package com.example.mybank.Authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybank.Database.Databasehelper;
import com.example.mybank.MainActivity;
import com.example.mybank.Models.User;
import com.example.mybank.R;
import com.example.mybank.Utils;

import java.sql.SQLException;

public class RegisterUser extends AppCompatActivity {
    private ImageView secondgirl, man, women, girl, boy, logo,logo2;
    private TextView textlisense, loginoption, warning,logintxt;
    private EditText address, name, password, email;
    private Button registerbutton;
    private Databasehelper databasehelper;
    private String Image_URL;
    private doesuserexist doesuserexist;
    private RegisterUsers registerUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        initViews();
        logintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterUser.this,LoginActivity.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        databasehelper = new Databasehelper(this);
        Image_URL="first";
        handle_Image_URL();
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initregister();
            }
        });
    }

    private void handle_Image_URL() {
        Log.d("TAG", "handle_Image_URL: started");
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image_URL="man";
            }
        });
        women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image_URL="women";
            }
        });
        girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image_URL="girl";
            }
        });
        secondgirl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image_URL="secondgirl";
            }
        });
        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image_URL="boy";
            }
        });

    }

    private void initregister() {
        Log.d("TAG", "initregister: started");
        String emailid = email.getText().toString();
        String Pass = password.getText().toString();
        if (emailid.equals("") || Pass.equals("")) {
            warning.setVisibility(View.VISIBLE);
            warning.setText("Please enter the Email and password");
        } else {
            warning.setVisibility(View.INVISIBLE);
            doesuserexist=new doesuserexist();
            doesuserexist.execute(emailid);
        }
    }

    private class doesuserexist extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                SQLiteDatabase db = databasehelper.getReadableDatabase();
                Cursor cursor = db.query("users", new String[]{"_id", "email"}, "email=?", new String[]{strings[0]}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        if(cursor.getString(cursor.getColumnIndex("email")).equals(strings[0])){
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
                        cursor.close();
                        db.close();
                        return false;
                    }
                } else {
                    db.close();
                return true;
                }


        }
            catch(Exception e)
          {  e.printStackTrace();
              return true;}
      // return null;
    }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
             warning.setVisibility(View.VISIBLE);
             warning.setText("This email already exists,Please try with another email");
            }
            else
            {
                warning.setVisibility(View.GONE);
                registerUser=new RegisterUsers();
                registerUser.execute();
            }
        }
    }
    private class RegisterUsers extends AsyncTask<Void,Void,User>{
  private String emailid;
  private String pass;
  private String Address;
  private String first_name;
  private String last_name;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String emailid=email.getText().toString();
            String pass=password.getText().toString();
            String Address=address.getText().toString();
            String Name=name.getText().toString();

            this.emailid=emailid;
            this.pass=pass;
            this.Address=Address;

            String[] names=Name.split(" ");
            if(names.length>=1)
            {    this.first_name=names[0];
              for(int i=1;i<names.length;i++)
              {
                  if(i>1)
                      last_name+=" "+names[i];
                  else  last_name+=names[i];
              }

            }

        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(null!=user)
            {
                Toast.makeText(RegisterUser.this, user.getFirst_name()+
                        user.getLast_name()+" added successfully. ", Toast.LENGTH_SHORT).show();
                Utils utils=new Utils(RegisterUser.this);
                utils.addusertosharedpreferences(user);
                Intent intent=new Intent(RegisterUser.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
            else
            {
                Toast.makeText(RegisterUser.this, "Sorry,Couldn't register the user,Please try again later."
                        , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected User doInBackground(Void... voids) {
            try{
                SQLiteDatabase db=databasehelper.getReadableDatabase();
                ContentValues values=new ContentValues();
                values.put("email", this.emailid);
                values.put("first_name", this.first_name);
                values.put("last_name", this.last_name);
                values.put("address", this.Address);
                values.put("password", this.pass);
                values.put("remained_amount",0.0);
                values.put("image_url",Image_URL);
                long user_id=  db.insert("users",null,values);
                Log.d("TAG", "doInBackground: user_id"+name);

                Cursor cursor=db.query("users",null,"_id=?",new String[]{String.valueOf(user_id)},
                        null,null,null);
              //  throw new SQLException();
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=doesuserexist)
        {
            if(!doesuserexist.isCancelled())
            {
                doesuserexist.cancel(true);
            }
        }
        if(null!=registerUser)
        {
            if(!registerUser.isCancelled())
            {
                registerUser.cancel(true);
            }
        }
    }

    private void initViews()
    {
        secondgirl=(ImageView)findViewById(R.id.secondgirl);
        man=(ImageView)findViewById(R.id.man);
        women=(ImageView)findViewById(R.id.women);
        girl=(ImageView)findViewById(R.id.girl);
        boy=(ImageView)findViewById(R.id.boy);
        logo=(ImageView)findViewById(R.id.logo);
        logo2=(ImageView)findViewById(R.id.logo2);
        textlisense=(TextView)findViewById(R.id.textlisense);
        loginoption=(TextView)findViewById(R.id.loginoption);
        warning=(TextView)findViewById(R.id.warning);
        logintxt=(TextView)findViewById(R.id.logintxt);
        address=(EditText) findViewById(R.id.address);
        name=(EditText)findViewById(R.id.name);
        password=(EditText) findViewById(R.id.password);
        email =(EditText)findViewById(R.id.email );
        registerbutton=(Button) findViewById(R.id.registerbutton );
    }
}
