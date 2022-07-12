package com.rsmnm.Utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rsmnm.Adapters.LocationAdapter;
import com.rsmnm.Models.BaseLocationItem;
import com.rsmnm.R;

import java.util.ArrayList;

public class DialogHelper {

    public static Dialog showAlertDialog(Context context, String message) {
        return new AlertDialog.Builder(context, R.style.ListDialog).setMessage(message).setPositiveButton("Ok", null).show();
    }

    public static Dialog showLocationPickerDialog(Context context, Object data, FilterDialogIface iface) {

        ArrayList<BaseLocationItem> list = null;
        try {
            list = (ArrayList<BaseLocationItem>) data;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Invalid Data Type", Toast.LENGTH_LONG).show();
        }

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.dialog_searchablelist, null, false);


        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ListDialog);
        builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ListView listView = dialogView.findViewById(R.id.listItems);
        SearchView searchView = dialogView.findViewById(R.id.search);
        TextView searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextColor(ContextCompat.getColor(context, R.color.white));
        searchView.setIconified(false);

        LocationAdapter adp = new LocationAdapter(context, list);
        listView.setAdapter(adp);

        listView.setOnItemClickListener((adapterView, view, poss, l) -> {
            iface.onItemSelected(adp.getItem(poss));
            dialog.dismiss();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adp.getFilter().filter(newText);
                return false;
            }
        });

        dialog.show();
        return dialog;
    }

    public interface FilterDialogIface {
        void onItemSelected(BaseLocationItem item);
    }
}
