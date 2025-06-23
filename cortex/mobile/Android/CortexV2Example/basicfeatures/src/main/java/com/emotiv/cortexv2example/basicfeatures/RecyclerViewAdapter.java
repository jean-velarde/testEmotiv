package com.emotiv.cortexv2example.basicfeatures;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolderCustom> {

    List<Data> list = Collections.emptyList();
    Context context;
    int currentPosition = -1;

    public RecyclerViewAdapter(List<Data> data, Application application) {
        this.list = data;
        this.context = application;
    }

    @NonNull
    @Override
    public ViewHolderCustom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        ViewHolderCustom holder = new ViewHolderCustom(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCustom holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).name);
        holder.name.setTextColor(Color.BLACK);
        holder.imageView.setImageResource(list.get(position).imageId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do on click
                Log.d("RecyclerView", "onClick：" + holder.getAdapterPosition());
                if (currentPosition >= 0) {
                    notifyItemChanged(currentPosition);
                }
                holder.name.setTextColor(Color.RED);
                currentPosition = holder.getAdapterPosition();

                Context context = holder.itemView.getContext();
                Log.d("class activity:", list.get(currentPosition).getClassActivity().toString());
                Intent intent = new Intent(context , list.get(currentPosition).getClassActivity());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
