package com.rsmnm.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.rsmnm.Fragments.passenger.SelectPickupDropFragment;
import com.rsmnm.Interfaces.LocationClickedInterface;
import com.rsmnm.Interfaces.WorkCompletedInterface;
import com.rsmnm.Models.LocationItem;
import com.rsmnm.R;
import com.rsmnm.Utils.AppConstants;
import com.rsmnm.Utils.DialogHelper;
import com.rsmnm.ViewHolders.DropOffHolder;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import hari.bounceview.BounceView;

public class DropoffItemsAdapter extends RecyclerView.Adapter<DropOffHolder> {

    ArrayList<LocationItem> list;
    Context context;
    LocationClickedInterface iface;

    public DropoffItemsAdapter(Context context, LocationClickedInterface iface) {
        list = new ArrayList<>();
        list.add(new LocationItem());
        this.context = context;
        this.iface = iface;
    }

    @NonNull
    @Override
    public DropOffHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_dropoffs, parent, false);
        return new DropOffHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DropOffHolder holder, int position) {

        if (position == 0) {
            holder.btn.setImageResource(R.drawable.icon_plus);
            if (list.size() < AppConstants.MAX_DROP_LIMIT)
                holder.btn.setVisibility(View.VISIBLE);
            else
                holder.btn.setVisibility(View.INVISIBLE);

            holder.btn.setOnClickListener(view -> {
                list.add(new LocationItem());
                notifyDataSetChanged();
            });
        } else {
            holder.btn.setImageResource(R.drawable.icon_delete);
            holder.btn.setOnClickListener(view -> {
                list.remove(position);
                notifyDataSetChanged();
            });
        }
        if (position == (list.size() - 1))
            holder.botSeperator.setVisibility(View.INVISIBLE);
        else
            holder.botSeperator.setVisibility(View.VISIBLE);

        BounceView.addAnimTo(holder.btn);

        holder.text.setText(list.get(position).address);

        holder.text.setOnClickListener(view -> iface.onLocationClicked(list.get(position)));
        holder.text.setOnLongClickListener(view -> {
                    new AlertDialog.Builder(context).setMessage("do you want to clear this drop-off ?").setPositiveButton("Yes", (dialogInterface, i) -> {
                        list.get(position).clear();
                        notifyDataSetChanged();
                    }).setNegativeButton("No", null).show();
                    return true;
                }
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void newLocationSelected(LocationItem locationItem) {

        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isEmpty()) {
                list.get(i).copy(locationItem);
                break;
            }
        }
        notifyDataSetChanged();

    }

    public ArrayList<LocationItem> getDropOffs() {
        ArrayList<LocationItem> tempList = new ArrayList<>(list);
        Iterator<LocationItem> iterator = tempList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isEmpty())
                iterator.remove();
        }
        return tempList;
    }
}
