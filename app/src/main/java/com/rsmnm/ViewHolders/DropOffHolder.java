package com.rsmnm.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rsmnm.R;

public class DropOffHolder extends RecyclerView.ViewHolder {

    public ImageView topSeperator, botSeperator;
    public TextView text;
    public ImageView btn;

    public DropOffHolder(View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.field);
        topSeperator = itemView.findViewById(R.id.seperator_top);
        botSeperator = itemView.findViewById(R.id.seperator_bot);
        btn = itemView.findViewById(R.id.btn);
    }

}
