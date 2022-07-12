package com.rsmnm.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsmnm.Interfaces.VehicleSelectedInterface;
import com.rsmnm.Models.VehicleTypeItem;
import com.rsmnm.R;
import com.rsmnm.ViewHolders.HomeVehicleTypeHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeVehicleTypesAdapter extends RecyclerView.Adapter<HomeVehicleTypeHolder> {

    private ArrayList<VehicleTypeItem> list;
    private VehicleSelectedInterface iface;
    private Context context;

    public HomeVehicleTypesAdapter(Context context, ArrayList<VehicleTypeItem> list, VehicleSelectedInterface iface) {
        this.list = list;
        this.context = context;
        this.iface = iface;
        setSelected(0);
    }

    private void setSelected(int selectedPos) {
        for (VehicleTypeItem item : list)
            item.selected = false;
        list.get(selectedPos).selected = true;
        notifyDataSetChanged();
        if (iface != null)
            iface.onVehicleTypeSelected(list.get(selectedPos));
    }

    @NonNull
    @Override
    public HomeVehicleTypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_home_vehicle_type, parent, false);
        return new HomeVehicleTypeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeVehicleTypeHolder holder, int position) {

        holder.getText().setText(list.get(position).title);
        if (!TextUtils.isEmpty(list.get(position).thumb_url))
            Picasso.get().load(list.get(position).thumb_url).fit().centerCrop().into(holder.getImg());

        if (position == list.size() - 1)
            holder.getSeperator().setVisibility(View.GONE);
        else
            holder.getSeperator().setVisibility(View.VISIBLE);

        if (list.get(position).selected)
            holder.getImg().setBackgroundResource(R.drawable.bg_carselection_active);
        else
            holder.getImg().setBackgroundResource(R.drawable.bg_carselection_inactive);

        holder.getParent().setOnClickListener(view -> setSelected(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
