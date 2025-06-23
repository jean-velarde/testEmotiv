package com.emotiv.cortexv2example.basicfeatures;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderCustom extends RecyclerView.ViewHolder {

    TextView name;
    ImageView imageView;

    ViewHolderCustom(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        imageView = (ImageView) itemView.findViewById(R.id.image);
    }
}
