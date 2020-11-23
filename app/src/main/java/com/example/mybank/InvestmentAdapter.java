package com.example.mybank;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.Models.Investment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentAdapter.ViewHolder> {
private Context context;
private int number=1;
    private static final String TAG ="InvestmentAdapter" ;
    private ArrayList<Investment> investments=new ArrayList<>() ;

    public InvestmentAdapter() {
    }

    public InvestmentAdapter(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private CardView parent;
        private TextView nametxt,Investmentnametxt, initdate,initdatetxt  ,investedamount  ,investedamounttxt,expectedprofit    ,expectedprofittxt ,finishdatetxt,finish_date, monthlyroitxt,   monthlytxt   ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent=(CardView)itemView.findViewById(R.id.parent);
            nametxt=(TextView) itemView.findViewById(R.id.nametxt);
            Investmentnametxt=(TextView) itemView.findViewById(R.id.Investmentnametxt);
            initdate=(TextView) itemView.findViewById(R.id.initdate);
            initdatetxt=(TextView) itemView.findViewById(R.id.initdatetxt);
            investedamount=(TextView) itemView.findViewById(R.id.investedamount);
            investedamounttxt=(TextView) itemView.findViewById(R.id.investedamounttxt);
            expectedprofit=(TextView) itemView.findViewById(R.id.expectedprofit);
            expectedprofittxt=(TextView) itemView.findViewById(R.id.expectedprofittxt);
            finishdatetxt=(TextView) itemView.findViewById(R.id.finishdatetxt);
            finish_date=(TextView) itemView.findViewById(R.id.finish_date);
            monthlytxt=(TextView) itemView.findViewById(R.id.monthlytxt);
            monthlyroitxt=(TextView) itemView.findViewById(R.id.monthlyroitxt);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_item_investment,parent,false);
        return new ViewHolder(view);
    }

    public void setInvestments(ArrayList<Investment> investments) {
        this.investments = investments;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder:started ");
        holder.Investmentnametxt.setText(investments.get(position).getName());
        holder.initdatetxt.setText(investments.get(position).getInit_date());
        holder.investedamounttxt.setText(String.valueOf(investments.get(position).getAmount()));
        holder.finish_date.setText(investments.get(position).getFinish_date());
        holder.monthlyroitxt.setText(String.valueOf(investments.get(position).getMonthly_roi()));
        holder.expectedprofittxt.setText(String.valueOf(getProfit(investments.get(position))+"%"));

        if(number==1)
        {
            holder.parent.setBackgroundColor(context.getResources().getColor(R.color.light_green));
            number=-1;
        }
        if(number==-1)
        {
            holder.parent.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
            number=1;
        }

    }

    private double getProfit(Investment investment) {
        Log.d(TAG, "getProfit: started"+investment.toString());
        Calendar calendar=Calendar.getInstance();
        double profit=0.0;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try{
          calendar.setTime((sdf).parse(investment.getInit_date()));
          int initmonth=calendar.get(Calendar.YEAR)*12+calendar.get(Calendar.MONTH);
          calendar.setTime(sdf.parse(investment.getFinish_date()));
          int finistmonths=calendar.get(Calendar.YEAR)*12+calendar.get(Calendar.MONTH);
          int months=finistmonths-initmonth;


          for(int i=0;i<months;i++)
          {
            profit+=(double) ( investment.getAmount()*investment.getMonthly_roi())/100 ;
          }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
        return profit;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+investments.size());
        return investments.size();
    }
}
