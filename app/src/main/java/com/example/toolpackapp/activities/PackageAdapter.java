package com.example.toolpackapp.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toolpackapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

public class PackageAdapter extends FirestoreRecyclerAdapter<packages, PackageAdapter.packageHolder> {

    public PackageAdapter(@NonNull FirestoreRecyclerOptions<packages> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull packageHolder holder, int position, @NonNull packages model) {
//        holder.deliveryDate.setText((CharSequence) model.getDate());
//        holder.DeliveryTime.setText((CharSequence) model.getTime());
        holder.packageDriver.setText(model.getDriver());
        holder.packageVendor.setText(model.getVendor());
        holder.packageStatus.setText(model.getStatus());
//        holder.date.setText(message.getDateTimeString());

    }

    @NonNull
    @Override
    public packageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_row,
                parent, false);
        return new packageHolder(v);
    }

    class packageHolder extends RecyclerView.ViewHolder {
        TextView packageDriver;
        //TextView DeliveryTime;
        // Button updateStatus;
        //TextView deliveryDate;
        // ImageView packagePhoto;
        TextView packageVendor;
        TextView packageStatus;
        private View itemview;

        public packageHolder(View itemview) {
            super(itemview);
            // Define click listener for the ViewHolder's View
            packageDriver = (TextView)  itemview.findViewById(R.id.packageDriver);
            //  packageDriver = (TextView) itemview.findViewById(R.id.pa);
            //  DeliveryTime = (TextView) itemview.findViewById(R.id.DeliveryTime);
            // deliveryDate = (TextView) itemview.findViewById(R.id.deliveryDate);
            // packagePhoto = (ImageView) itemview.findViewById(R.id.packagePhoto);
            packageVendor = (TextView) itemview.findViewById(R.id.packageVendor);
            packageStatus = (TextView) itemview.findViewById(R.id.packageStatus);
            // this.itemview = itemview;
        }
    }


}
