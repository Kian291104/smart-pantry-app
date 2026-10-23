package com.example.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.Format;

import java.util.ArrayList;
import java.util.List;

/** Custom RecyclerView adapter that binds PantryItem rows from the database to item_pantry.xml. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onItemClick(PantryItem item);   // -> edit
        void onDeleteClick(PantryItem item); // -> confirm + delete
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private boolean highlightExpiry = true;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems, boolean highlightExpiry) {
        this.highlightExpiry = highlightExpiry;
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        PantryItem item = items.get(position);
        h.name.setText(item.getName());
        h.quantity.setText(Format.qtyWithUnit(item.getQuantity(), item.getUnit()));

        Integer days = Format.daysUntil(item.getExpiryDate());
        if (days == null) {
            h.expiry.setVisibility(View.GONE);
        } else {
            h.expiry.setVisibility(View.VISIBLE);
            int color = R.color.text_secondary;
            String label;
            if (days < 0) {
                label = "Expired " + item.getExpiryDate();
                if (highlightExpiry) color = R.color.expired_red;
            } else if (days <= Format.EXPIRING_SOON_DAYS) {
                label = days == 0 ? "Expires today" : "Expires in " + days + " day" + (days == 1 ? "" : "s");
                if (highlightExpiry) color = R.color.accent_orange;
            } else {
                label = "Expires " + item.getExpiryDate();
            }
            h.expiry.setText(label);
            h.expiry.setTextColor(ContextCompat.getColor(h.itemView.getContext(), color));
        }

        h.itemView.setOnClickListener(v -> listener.onItemClick(item));
        h.delete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Holds references to row views so findViewById isn't repeated while scrolling. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final ImageButton delete;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.text_name);
            quantity = v.findViewById(R.id.text_quantity);
            expiry = v.findViewById(R.id.text_expiry);
            delete = v.findViewById(R.id.button_delete);
        }
    }
}
