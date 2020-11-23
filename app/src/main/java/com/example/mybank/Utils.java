package com.example.mybank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mybank.Authentication.RegisterUser;
import com.example.mybank.Models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Utils {

    private Context context;
    private static final String TAG="Utils";

    public Utils(Context context) {
        this.context = context;
    }
    public void addusertosharedpreferences(User user)
    {
        Log.d(TAG, "addusertosharedpreferences: adding "+user.toString());
        SharedPreferences sharedPreferences=context.getSharedPreferences("logged in user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson=new Gson();
        editor.putString("user",gson.toJson(user));
        editor.apply();
    }
    public User isUserLoggedIn()
    {
        Log.d(TAG, "isUserLoggedIn: started");
        SharedPreferences sharedPreferences=context.getSharedPreferences("logged in user",Context.MODE_PRIVATE);
        Gson gson=new Gson();
        Type type=new TypeToken<User>(){}.getType();
      return gson.fromJson(sharedPreferences.getString("user",null),type);
       // return user;
    }



}
