package com.example.mybank.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybank.Models.Loan;
import com.example.mybank.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Loan_Adapter extends  RecyclerView.Adapter<Loan_Adapter.ViewHolder> {
    private int number=-1;
    private static  final String TAG="Loan_Adapter";
    private Context context;
    private ArrayList<Loan> loans=new ArrayList<>();
    public Loan_Adapter(Context context) {
        this.context = context;
    }

    public Loan_Adapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loans,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Investmentnametxt.setText(loans.get(position).getName());
        holder.initdatetxt.setText(loans.get(position).getInit_date());
        holder.investedamounttxt.setText(String.valueOf(loans.get(position).getInit_amount()));
        holder.finish_date.setText(loans.get(position).getFinish_date());
        holder.monthlyroitxt.setText(String.valueOf(loans.get(position).getMonthly_roi())+"%");
        holder.monthlypaymenttxt.setText(" Rs. "+String.valueOf(loans.get(position).getMonthly_payment()));
        holder.monthly_payment_edt_txt.setText(String.valueOf(loans.get(position).getRemained_amount()));
        holder.expectedprofittxt.setText(getTotalloss(loans.get(position)) + "%");

//holder.totalloannamountedttxt.setText(loans.get(position).ge);
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

    private double getTotalloss(Loan loan) {
        Log.d(TAG, "getTotalloss: started for"+loans.toString());
        double loss=0.0;
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try{
            calendar.setTime((sdf).parse(loan.getInit_date()));
            int initmonth=calendar.get(Calendar.YEAR)*12+calendar.get(Calendar.MONTH);
            calendar.setTime(sdf.parse(loan.getFinish_date()));
            int finistmonths=calendar.get(Calendar.YEAR)*12+calendar.get(Calendar.MONTH);
            int months=finistmonths-initmonth;


            for(int i=0;i<months;i++)
            {
               loss+=(Double)( (Double)(loan.getInit_amount()*loan.getMonthly_roi())/100) ;
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
        return loss;
    }



    @Override
    public int getItemCount() {
        return loans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView Investmentnametxt,initdatetxt,investedamounttxt,expectedprofittxt,finish_date,
                monthlyroitxt,monthlypaymenttxt,monthly_payment_edt_txt,totalloannamountedttxt;

        private CardView parent;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Investmentnametxt=(TextView)itemView.findViewById(R.id.Investmentnametxt);
            initdatetxt=(TextView)itemView.findViewById(R.id.initdatetxt);
            investedamounttxt=(TextView)itemView.findViewById(R.id.investedamounttxt);
            expectedprofittxt=(TextView)itemView.findViewById(R.id.expectedprofittxt);
            finish_date=(TextView)itemView.findViewById(R.id.finish_date);
            monthlyroitxt=(TextView)itemView.findViewById(R.id.monthlyroitxt);
            monthlypaymenttxt=(TextView)itemView.findViewById(R.id.monthlypaymenttxt);
            monthly_payment_edt_txt=(TextView)itemView.findViewById(R.id.monthly_payment_edt_txt);
            totalloannamountedttxt=(TextView)itemView.findViewById(R.id.totalloannamountedttxt);
            parent=(CardView)itemView.findViewById(R.id.parent);
        }
    }

    public ArrayList<Loan> getLoans() {
        return loans;
    }

    public void setLoans(ArrayList<Loan> loans) {
        this.loans = loans;
        notifyDataSetChanged();
    }
}
