package com.example.vanur.ocrnumberplatedetection.User.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vanur.ocrnumberplatedetection.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {

    List<FireModel> list;
    Context context;

    public RecyclerAdapter(List<FireModel> list, Context context){
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card,parent,false);
        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyHolder holder, int position) {
        FireModel myList= list.get(position);
        holder.toll_Name.setText(myList.getTollName());
        holder.trans_Id.setText(myList.getTransId());
        holder.amount_toll.setText(myList.getxAmount()+"/-");
        holder.date_toll.setText(myList.getDate());
    }

    @Override
    public int getItemCount() {
        int arr =0;
        try{
            if(list.size()==0){
                arr=0;
            }
            else{
                arr = list.size();
            }
        }catch (Exception e){

        }
        return arr;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView toll_Name, trans_Id, amount_toll, date_toll;


        public MyHolder(View itemView) {
            super(itemView);
            toll_Name = (TextView) itemView.findViewById(R.id.tollName);
            trans_Id= (TextView) itemView.findViewById(R.id.transId);
            amount_toll = (TextView) itemView.findViewById(R.id.amount);
            date_toll = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
