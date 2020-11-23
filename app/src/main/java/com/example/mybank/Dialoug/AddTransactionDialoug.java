package com.example.mybank.Dialoug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mybank.AddInvestment;
import com.example.mybank.LoanAcivity;
import com.example.mybank.R;
//import com.example.mybank.Shopping_Activity;
import com.example.mybank.TransferActivity;

public class AddTransactionDialoug extends DialogFragment {
    private RelativeLayout firstrelative,secondrelative,thirdrelative,fourthrelative;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view=getActivity().getLayoutInflater().inflate(R.layout.dialog_add_transaction,null);
        super.onCreate(savedInstanceState);
        firstrelative=view.findViewById(R.id.firstrelative);
        secondrelative=view.findViewById(R.id.secondrelative);
        thirdrelative=view.findViewById(R.id.thirdrelative);
        fourthrelative=view.findViewById(R.id.fourthrelative);


        secondrelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddInvestment.class);
                startActivity(intent);
            }
        });
        thirdrelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), LoanAcivity.class);
                startActivity(intent);
            }
        });
        fourthrelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), TransferActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity()).
                setTitle("Add Transaction").
                setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).
                setView(view);
        return builder.create();
    }
}
